package monitor

import (
	"log"
	"os"
	"runtime"
	"time"

	"inframirror-agent/internal/api"
	"inframirror-agent/internal/cache"
)

type InstanceMonitor struct {
	client       *api.Client
	cacheManager *cache.Manager
	instanceID   int64
	pingInterval time.Duration
	hwInterval   time.Duration
	stopChan     chan struct{}
}

func NewInstanceMonitor(client *api.Client, cacheManager *cache.Manager, pingInterval, hwInterval int) *InstanceMonitor {
	return &InstanceMonitor{
		client:       client,
		cacheManager: cacheManager,
		pingInterval: time.Duration(pingInterval) * time.Second,
		hwInterval:   time.Duration(hwInterval) * time.Second,
		stopChan:     make(chan struct{}),
	}
}

func (m *InstanceMonitor) Start() error {
	// Ensure instance exists
	if err := m.ensureInstance(); err != nil {
		return err
	}

	// Start monitoring loops
	go m.pingLoop()
	go m.hardwareLoop()

	return nil
}

func (m *InstanceMonitor) Stop() {
	close(m.stopChan)
}

func (m *InstanceMonitor) ensureInstance() error {
	cachedData := m.cacheManager.GetCache()
	
	if m.cacheManager.HasInstanceCache() {
		log.Printf("Using cached instance ID: %d", cachedData.InstanceID)
		m.instanceID = cachedData.InstanceID
		return nil
	}

	log.Println("Creating instance for this agent...")
	
	hostname := getHostname()
	privateIP := getPrivateIP()
	osType := runtime.GOOS
	platform := getPlatform()

	instance, err := m.client.CreateInstance(
		hostname,           // name
		hostname,           // hostname
		osType,            // osType
		platform,          // platform
		privateIP,         // privateIP
		cachedData.DatacenterID,
		cachedData.AgentID,
	)
	if err != nil {
		return err
	}

	log.Printf("Instance created with ID: %d", instance.ID)
	m.instanceID = instance.ID

	// Update cache with instance ID
	if err := m.cacheManager.UpdateCache(
		cachedData.AgentID,
		cachedData.RegionID,
		cachedData.DatacenterID,
		instance.ID,
		cachedData.Region,
		cachedData.Datacenter,
	); err != nil {
		log.Printf("Warning: Failed to cache instance ID: %v", err)
	}

	return nil
}

func (m *InstanceMonitor) pingLoop() {
	ticker := time.NewTicker(m.pingInterval)
	defer ticker.Stop()

	for {
		select {
		case <-ticker.C:
			m.sendPingHeartbeat()
		case <-m.stopChan:
			return
		}
	}
}

func (m *InstanceMonitor) hardwareLoop() {
	ticker := time.NewTicker(m.hwInterval)
	defer ticker.Stop()

	for {
		select {
		case <-ticker.C:
			m.sendHardwareHeartbeat()
		case <-m.stopChan:
			return
		}
	}
}

func (m *InstanceMonitor) sendPingHeartbeat() {
	// TODO: Implement actual ping heartbeat submission
	log.Printf("Sending ping heartbeat for instance %d", m.instanceID)
}

func (m *InstanceMonitor) sendHardwareHeartbeat() {
	// TODO: Implement actual hardware heartbeat submission
	log.Printf("Sending hardware heartbeat for instance %d", m.instanceID)
}

func getHostname() string {
	hostname, err := os.Hostname()
	if err != nil {
		return "unknown"
	}
	return hostname
}

func getPrivateIP() string {
	// TODO: Implement actual private IP detection
	return "127.0.0.1"
}

func getPlatform() string {
	// TODO: Implement actual platform detection
	return "Unknown"
}