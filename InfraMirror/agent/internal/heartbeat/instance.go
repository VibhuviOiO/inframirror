package heartbeat

import (
	"log"
	"time"

	"inframirror-agent/internal/api"
	"inframirror-agent/internal/cache"
	"inframirror-agent/internal/system"
)

type InstanceHeartbeatManager struct {
	client       *api.Client
	cacheManager *cache.Manager
	sysInfo      *system.Info
	instanceID   int64
	pingInterval time.Duration
	hwInterval   time.Duration
	hwEnabled    bool
	stopChan     chan struct{}
}

func NewInstanceHeartbeatManager(client *api.Client, cacheManager *cache.Manager, pingInterval, hwInterval int, hwEnabled bool) *InstanceHeartbeatManager {
	return &InstanceHeartbeatManager{
		client:       client,
		cacheManager: cacheManager,
		sysInfo:      system.NewInfo(),
		pingInterval: time.Duration(pingInterval) * time.Second,
		hwInterval:   time.Duration(hwInterval) * time.Second,
		hwEnabled:    hwEnabled,
		stopChan:     make(chan struct{}),
	}
}

func (m *InstanceHeartbeatManager) Start() error {
	cachedData := m.cacheManager.GetCache()
	
	if !m.cacheManager.HasInstanceCache() {
		if err := m.createInstance(); err != nil {
			return err
		}
	}
	
	m.instanceID = cachedData.InstanceID
	log.Printf("Starting instance heartbeats for instance %d", m.instanceID)

	go m.pingLoop()
	
	if m.hwEnabled {
		log.Printf("Hardware monitoring enabled, starting hardware loop with %v interval", m.hwInterval)
		go m.hardwareLoop()
	} else {
		log.Printf("Hardware monitoring disabled")
	}
	
	return nil
}

func (m *InstanceHeartbeatManager) Stop() {
	close(m.stopChan)
}

func (m *InstanceHeartbeatManager) GetType() string {
	return "instance"
}

func (m *InstanceHeartbeatManager) createInstance() error {
	cachedData := m.cacheManager.GetCache()
	hostname := m.sysInfo.GetHostname()
	
	instance, err := m.client.CreateInstance(
		hostname,
		hostname,
		m.sysInfo.GetOSType(),
		m.sysInfo.GetPlatform(),
		m.sysInfo.GetPrivateIP(),
		cachedData.DatacenterID,
		cachedData.AgentID,
	)
	if err != nil {
		return err
	}

	log.Printf("Instance created with ID: %d", instance.ID)
	
	return m.cacheManager.UpdateCache(
		cachedData.AgentID,
		cachedData.RegionID,
		cachedData.DatacenterID,
		instance.ID,
		cachedData.Region,
		cachedData.Datacenter,
	)
}

func (m *InstanceHeartbeatManager) pingLoop() {
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

func (m *InstanceHeartbeatManager) hardwareLoop() {
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

func (m *InstanceHeartbeatManager) sendPingHeartbeat() {
	// Create ping heartbeat
	heartbeat := map[string]interface{}{
		"instanceId":     m.instanceID,
		"executedAt":     time.Now().Format(time.RFC3339),
		"heartbeatType":  "PING",
		"success":        true,
		"status":         "UP",
		"responseTimeMs": 5,  // Simulated ping time
		"packetLoss":     0.0,
		"jitterMs":       1,
	}

	if err := m.client.SubmitInstanceHeartbeat(heartbeat); err != nil {
		log.Printf("Failed to submit ping heartbeat: %v", err)
	} else {
		log.Printf("Ping heartbeat submitted for instance %d", m.instanceID)
	}
}

func (m *InstanceHeartbeatManager) sendHardwareHeartbeat() {
	// Get network stats
	rxBytes, txBytes := m.sysInfo.GetNetworkStats()
	
	// Create hardware heartbeat with real system metrics
	heartbeat := map[string]interface{}{
		"instanceId":      m.instanceID,
		"executedAt":      time.Now().Format(time.RFC3339),
		"heartbeatType":   "HARDWARE",
		"success":         true,
		"status":          "UP",
		"cpuUsage":        m.sysInfo.GetCPUUsage(),
		"memoryUsage":     m.sysInfo.GetMemoryUsage(),
		"diskUsage":       m.sysInfo.GetDiskUsage(),
		"loadAverage":     m.sysInfo.GetLoadAverage(),
		"processCount":    m.sysInfo.GetProcessCount(),
		"networkRxBytes":  rxBytes,
		"networkTxBytes":  txBytes,
		"uptimeSeconds":   86400, // TODO: Get actual uptime
	}

	if err := m.client.SubmitInstanceHeartbeat(heartbeat); err != nil {
		log.Printf("Failed to submit hardware heartbeat: %v", err)
	} else {
		log.Printf("Hardware heartbeat submitted for instance %d", m.instanceID)
	}
}