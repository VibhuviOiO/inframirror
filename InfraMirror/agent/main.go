package main

import (
	"flag"
	"log"
	"net"
	"os"
	"os/exec"
	"os/signal"
	"runtime"
	"strings"
	"syscall"
	"time"

	"inframirror-agent/config"
	"inframirror-agent/internal/api"
	"inframirror-agent/internal/cache"
	"inframirror-agent/internal/heartbeat"
	"inframirror-agent/internal/lock"
	"inframirror-agent/internal/monitor/http"
	"inframirror-agent/internal/startup"
)

func main() {
	globalConfig := flag.String("global", "conf/global.yml", "Path to global configuration file")
	configDir := flag.String("config-dir", "./conf", "Directory containing configuration files")
	dataDir := flag.String("data", "./data", "Data directory for persistent storage")
	flag.Parse()

	// Load configuration
	cfg, err := config.LoadConfig(*globalConfig, *configDir)
	if err != nil {
		log.Fatalf("Failed to load config: %v", err)
	}

	// Initialize cache manager
	cacheManager := cache.NewManager(*dataDir)
	if err := cacheManager.Load(); err != nil {
		log.Fatalf("Failed to load cache: %v", err)
	}

	log.Printf("Starting InfraMirror Agent: %s", cfg.Name)

	// Display configuration
	log.Println("=")
	log.Println("Configuration Loaded:")
	log.Printf("  Agent Name: %s", cfg.Name)
	log.Printf("  Region: %s", cfg.Region)
	log.Printf("  Datacenter: %s", cfg.Datacenter)
	log.Printf("  Backend URL: %s", cfg.Backend.URL)
	if len(cfg.APIKey) > 28 {
		log.Printf("  API Key: %s...%s", cfg.APIKey[:20], cfg.APIKey[len(cfg.APIKey)-8:])
	} else {
		log.Printf("  API Key: %s", "***hidden***")
	}
	log.Println("=")
	log.Println("Enabled Modules:")
	if cfg.Instances.Enable {
		log.Printf("  ✓ Instance Monitoring (ping: %ds, hardware: %ds)", cfg.Instances.PingInterval, cfg.Instances.HardwareMonitoring.Interval)
	} else {
		log.Println("  ✗ Instance Monitoring (disabled)")
	}
	if cfg.HTTP != nil && cfg.HTTP.Enable {
		log.Printf("  ✓ HTTP Monitoring (%d monitors configured)", len(cfg.HTTP.Monitors))
	} else {
		log.Println("  ✗ HTTP Monitoring (disabled)")
	}
	log.Println("=")

	// Parse timeout
	timeout, err := time.ParseDuration(cfg.Backend.Timeout)
	if err != nil {
		timeout = 10 * time.Second
	}

	// Set API config from backend config
	if cfg.API.URL == "" {
		cfg.API.URL = cfg.Backend.URL
	}
	if cfg.API.Key == "" {
		cfg.API.Key = cfg.APIKey
	}
	if cfg.API.Timeout == 0 {
		cfg.API.Timeout = timeout
	}

	// Create API client
	apiClient := api.NewClient(cfg.Backend.URL, cfg.APIKey, timeout)

	// Create startup validator
	validator := startup.NewValidator(cfg.Backend.URL, cfg.APIKey, apiClient, cacheManager)

	// Step 1: Check backend health (blocks until backend is UP)
	log.Println("🔍 Step 1: Checking backend health...")
	validator.CheckBackendHealth()

	// Step 2: Validate API key
	log.Println("🔍 Step 2: Validating API key...")
	if err := validator.ValidateAPIKey(); err != nil {
		log.Fatalf("API key validation failed: %v", err)
	}

	// Step 3: Validate cached resources
	log.Println("🔍 Step 3: Validating cached resources...")
	cacheValid := validator.ValidateCachedResources()

	log.Println("=")

	if cacheValid {
		cachedData := cacheManager.GetCache()
		log.Printf("Using cached data - Agent: %d, Region: %d, Datacenter: %d", 
			cachedData.AgentID, cachedData.RegionID, cachedData.DatacenterID)
		cfg.AgentID = cachedData.AgentID
		cfg.RegionID = cachedData.RegionID
		cfg.DatacenterID = cachedData.DatacenterID
	} else {
		// Register agent and cache results
		agentResp, err := registerAgent(apiClient, cfg)
		if err != nil {
			log.Fatalf("Failed to register agent: %v", err)
		}

		log.Printf("Agent registered successfully with ID: %d", agentResp.AgentID)

		// Cache the results
		if err := cacheManager.UpdateCache(agentResp.AgentID, agentResp.Region.ID, 
			agentResp.Datacenter.ID, 0, cfg.Region, cfg.Datacenter); err != nil {
			log.Printf("Warning: Failed to cache agent data: %v", err)
		}

		cfg.AgentID = agentResp.AgentID
		cfg.RegionID = agentResp.Region.ID
		cfg.DatacenterID = agentResp.Datacenter.ID
	}

	// Initialize lock manager for HA
	lockManager := lock.NewManager(cfg.AgentID)
	lockManager.Start()
	defer lockManager.Stop()

	// Wait for leadership
	for !lockManager.IsLeader() {
		log.Println("Waiting for leadership...")
		time.Sleep(5 * time.Second)
	}

	log.Println("Agent is now the leader, starting monitoring...")

	log.Println("=")
	log.Println("Starting Monitoring Services:")

	// Create HTTP monitors from configuration
	if cfg.HTTP != nil && cfg.HTTP.Enable {
		log.Println("  Creating HTTP monitors from configuration...")
		if err := http.CreateHTTPMonitors(cfg, cacheManager); err != nil {
			log.Printf("  ⚠️  Failed to create HTTP monitors: %v", err)
		}
	}

	// Start monitoring if instances are enabled
	if cfg.Instances.Enable {
		instanceManager := heartbeat.NewInstanceHeartbeatManager(apiClient, cacheManager, 
			cfg.Instances.PingInterval, cfg.Instances.HardwareMonitoring.Interval, cfg.Instances.HardwareMonitoring.Enable)
		if err := instanceManager.Start(); err != nil {
			log.Fatalf("Failed to start instance monitoring: %v", err)
		}
		defer instanceManager.Stop()
		log.Println("  ✓ Instance monitoring started")
	} else {
		log.Println("  ✗ Instance monitoring disabled")
	}

	// Start HTTP monitoring
	httpManager := heartbeat.NewHttpHeartbeatManager(apiClient, cacheManager)
	if err := httpManager.Start(); err != nil {
		log.Printf("  ✗ HTTP monitoring: %v", err)
	} else {
		defer httpManager.Stop()
		log.Println("  ✓ HTTP monitoring started")
	}

	// Start agent heartbeat
	log.Printf("  ✓ Agent heartbeat (every %ds)", cfg.Agent.HeartbeatInterval)
	go startAgentHeartbeat(apiClient, cfg.Agent.HeartbeatInterval)

	// Start backend health monitor
	log.Println("  ✓ Backend health monitor (every 30s)")
	go validator.WaitForBackend()

	log.Println("=")
	log.Println("✅ All services started successfully")

	// Wait for shutdown signal
	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)
	<-sigChan

	log.Println("Shutting down agent...")
}

func registerAgent(client *api.Client, cfg *config.Config) (*api.AgentRegistrationResponse, error) {
	hostname, _ := os.Hostname()
	
	req := &api.AgentRegistrationRequest{
		Name:         cfg.Name,
		Hostname:     hostname,
		IPAddress:    getLocalIP(),
		OSType:       runtime.GOOS,
		OSVersion:    getOSVersion(),
		AgentVersion: "1.0.0",
		Tags: map[string]string{
			"region":     cfg.Region,
			"datacenter": cfg.Datacenter,
			"environment": "production",
		},
	}

	return client.RegisterAgent(req)
}

func getOSVersion() string {
	switch runtime.GOOS {
	case "darwin":
		out, err := exec.Command("sw_vers", "-productVersion").Output()
		if err == nil {
			return "macOS " + strings.TrimSpace(string(out))
		}
	case "linux":
		if data, err := os.ReadFile("/etc/os-release"); err == nil {
			for _, line := range strings.Split(string(data), "\n") {
				if strings.HasPrefix(line, "PRETTY_NAME=") {
					return strings.Trim(strings.TrimPrefix(line, "PRETTY_NAME="), `"`)
				}
			}
		}
	case "windows":
		out, err := exec.Command("cmd", "/c", "ver").Output()
		if err == nil {
			return strings.TrimSpace(string(out))
		}
	}
	return runtime.GOOS
}

func startAgentHeartbeat(client *api.Client, intervalSeconds int) {
	ticker := time.NewTicker(time.Duration(intervalSeconds) * time.Second)
	defer ticker.Stop()

	for range ticker.C {
		if err := client.SendHeartbeat(); err != nil {
			log.Printf("⚠️  Failed to send agent heartbeat: %v (will retry)", err)
		} else {
			log.Println("✅ Agent heartbeat sent successfully")
		}
	}
}

func getLocalIP() string {
	addrs, err := net.InterfaceAddrs()
	if err != nil {
		return "127.0.0.1"
	}

	for _, addr := range addrs {
		if ipnet, ok := addr.(*net.IPNet); ok && !ipnet.IP.IsLoopback() {
			if ipnet.IP.To4() != nil {
				return ipnet.IP.String()
			}
		}
	}
	return "127.0.0.1"
}