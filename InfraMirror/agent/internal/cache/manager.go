package cache

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
)

type AgentCache struct {
	AgentID           int64            `json:"agentId"`
	RegionID          int64            `json:"regionId"`
	DatacenterID      int64            `json:"datacenterId"`
	InstanceID        int64            `json:"instanceId"`
	Region            string           `json:"region"`
	Datacenter        string           `json:"datacenter"`
	HTTPMonitors      map[string]int64 `json:"httpMonitors"`
	CassandraServices map[string]int64 `json:"cassandraServices"`
	ServiceInstances  map[string]int64 `json:"serviceInstances"`
	InstancesByHost   map[string]int64 `json:"instancesByHost"`
}

type Manager struct {
	dataDir   string
	cacheFile string
	cache     *AgentCache
}

func NewManager(dataDir string) *Manager {
	cacheFile := filepath.Join(dataDir, "agent-cache.json")
	return &Manager{
		dataDir:   dataDir,
		cacheFile: cacheFile,
		cache: &AgentCache{
			HTTPMonitors:      make(map[string]int64),
			CassandraServices: make(map[string]int64),
			ServiceInstances:  make(map[string]int64),
			InstancesByHost:   make(map[string]int64),
		},
	}
}

func (m *Manager) Load() error {
	// Create data directory if not exists
	if err := os.MkdirAll(m.dataDir, 0755); err != nil {
		return fmt.Errorf("failed to create data directory: %w", err)
	}

	// Load cache from file if exists
	if _, err := os.Stat(m.cacheFile); os.IsNotExist(err) {
		return nil // No cache file, start fresh
	}

	data, err := os.ReadFile(m.cacheFile)
	if err != nil {
		return fmt.Errorf("failed to read cache file: %w", err)
	}

	if err := json.Unmarshal(data, m.cache); err != nil {
		return fmt.Errorf("failed to parse cache file: %w", err)
	}

	return nil
}

func (m *Manager) Save() error {
	data, err := json.MarshalIndent(m.cache, "", "  ")
	if err != nil {
		return fmt.Errorf("failed to marshal cache: %w", err)
	}

	if err := os.WriteFile(m.cacheFile, data, 0644); err != nil {
		return fmt.Errorf("failed to write cache file: %w", err)
	}

	return nil
}

func (m *Manager) GetCache() *AgentCache {
	return m.cache
}

func (m *Manager) UpdateCache(agentID, regionID, datacenterID, instanceID int64, region, datacenter string) error {
	m.cache.AgentID = agentID
	m.cache.RegionID = regionID
	m.cache.DatacenterID = datacenterID
	m.cache.InstanceID = instanceID
	m.cache.Region = region
	m.cache.Datacenter = datacenter
	return m.Save()
}

func (m *Manager) HasCache() bool {
	return m.cache.AgentID > 0 && m.cache.RegionID > 0 && m.cache.DatacenterID > 0
}

func (m *Manager) HasInstanceCache() bool {
	return m.HasCache() && m.cache.InstanceID > 0
}

func (m *Manager) ClearCache() error {
	m.cache = &AgentCache{
		HTTPMonitors:      make(map[string]int64),
		CassandraServices: make(map[string]int64),
		ServiceInstances:  make(map[string]int64),
		InstancesByHost:   make(map[string]int64),
	}
	if err := os.Remove(m.cacheFile); err != nil && !os.IsNotExist(err) {
		return fmt.Errorf("failed to remove cache file: %w", err)
	}
	return nil
}

func (m *Manager) SetHTTPMonitor(name string, id int64) {
	if m.cache.HTTPMonitors == nil {
		m.cache.HTTPMonitors = make(map[string]int64)
	}
	m.cache.HTTPMonitors[name] = id
	m.Save()
}

func (m *Manager) GetHTTPMonitor(name string) (int64, bool) {
	if m.cache.HTTPMonitors == nil {
		return 0, false
	}
	id, ok := m.cache.HTTPMonitors[name]
	return id, ok
}

func (m *Manager) SetCassandraService(name string, id int64) {
	if m.cache.CassandraServices == nil {
		m.cache.CassandraServices = make(map[string]int64)
	}
	m.cache.CassandraServices[name] = id
	m.Save()
}

func (m *Manager) GetCassandraService(name string) int64 {
	if m.cache.CassandraServices == nil {
		return 0
	}
	return m.cache.CassandraServices[name]
}

func (m *Manager) SetServiceInstance(key string, id int64) {
	if m.cache.ServiceInstances == nil {
		m.cache.ServiceInstances = make(map[string]int64)
	}
	m.cache.ServiceInstances[key] = id
	m.Save()
}

func (m *Manager) GetServiceInstance(key string) int64 {
	if m.cache.ServiceInstances == nil {
		return 0
	}
	return m.cache.ServiceInstances[key]
}

func (m *Manager) SetInstanceByHostname(hostname string, id int64) {
	if m.cache.InstancesByHost == nil {
		m.cache.InstancesByHost = make(map[string]int64)
	}
	m.cache.InstancesByHost[hostname] = id
	m.Save()
}

func (m *Manager) GetInstanceByHostname(hostname string) int64 {
	if m.cache.InstancesByHost == nil {
		return 0
	}
	return m.cache.InstancesByHost[hostname]
}