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
	stopChan     chan struct{}
}

func NewInstanceHeartbeatManager(client *api.Client, cacheManager *cache.Manager, pingInterval, hwInterval int) *InstanceHeartbeatManager {
	return &InstanceHeartbeatManager{
		client:       client,
		cacheManager: cacheManager,
		sysInfo:      system.NewInfo(),
		pingInterval: time.Duration(pingInterval) * time.Second,
		hwInterval:   time.Duration(hwInterval) * time.Second,
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
	go m.hardwareLoop()
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
	// TODO: Implement actual ping and batch submission
	log.Printf("Ping heartbeat for instance %d", m.instanceID)
}

func (m *InstanceHeartbeatManager) sendHardwareHeartbeat() {
	// TODO: Implement actual hardware metrics and batch submission
	log.Printf("Hardware heartbeat for instance %d", m.instanceID)
}