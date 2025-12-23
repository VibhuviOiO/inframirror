package main

import (
	"flag"
	"log"
	"os"
	"os/signal"
	"runtime"
	"syscall"
	"time"

	"inframirror-agent/config"
	"inframirror-agent/internal/api"
	"inframirror-agent/internal/cache"
	"inframirror-agent/internal/heartbeat"
	"inframirror-agent/internal/lock"
	"inframirror-agent/internal/startup"
)

func main() {
	globalConfig := flag.String("global", "global.yml", "Path to global configuration file")
	instanceConfig := flag.String("instance", "example/instance/config.yml", "Path to instance configuration file")
	dataDir := flag.String("data", "./data", "Data directory for persistent storage")
	flag.Parse()

	// Load configuration
	cfg, err := config.LoadConfig(*globalConfig, *instanceConfig)
	if err != nil {
		log.Fatalf("Failed to load config: %v", err)
	}

	// Initialize cache manager
	cacheManager := cache.NewManager(*dataDir)
	if err := cacheManager.Load(); err != nil {
		log.Fatalf("Failed to load cache: %v", err)
	}

	log.Printf("Starting InfraMirror Agent: %s", cfg.Name)

	// Parse timeout
	timeout, err := time.ParseDuration(cfg.Backend.Timeout)
	if err != nil {
		timeout = 10 * time.Second
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

	// Start monitoring if instances are enabled
	if cfg.Instances.Enable {
		instanceManager := heartbeat.NewInstanceHeartbeatManager(apiClient, cacheManager, 
			cfg.Instances.PingInterval, cfg.Instances.HardwareMonitoring.Interval, cfg.Instances.HardwareMonitoring.Enable)
		if err := instanceManager.Start(); err != nil {
			log.Fatalf("Failed to start instance monitoring: %v", err)
		}
		defer instanceManager.Stop()
		log.Println("Instance monitoring started")
	}

	// Start HTTP monitoring
	httpManager := heartbeat.NewHttpHeartbeatManager(apiClient, cacheManager)
	if err := httpManager.Start(); err != nil {
		log.Printf("Warning: Failed to start HTTP monitoring: %v", err)
	} else {
		defer httpManager.Stop()
		log.Println("HTTP monitoring started")
	}

	// Start agent heartbeat (every N seconds)
	go startAgentHeartbeat(apiClient, cfg.Agent.HeartbeatInterval)

	// Start backend health monitor
	go validator.WaitForBackend()

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
	// TODO: Implement actual OS version detection
	return "Unknown"
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
	// TODO: Implement actual IP detection
	return "127.0.0.1"
}