package cassandra

import (
	"bytes"
	"encoding/json"
	"fmt"
	"inframirror-agent/config"
	"inframirror-agent/internal/api"
	"inframirror-agent/internal/cache"
	"log"
	"time"
)

type ServiceHeartbeat struct {
	MonitoredServiceID int64  `json:"monitoredServiceId"`
	ServiceInstanceID  *int64 `json:"serviceInstanceId"`
	ExecutedAt         string `json:"executedAt"`
	Success            bool   `json:"success"`
	Status             string `json:"status"`
	ResponseTimeMs     int    `json:"responseTimeMs"`
	ErrorMessage       string `json:"errorMessage,omitempty"`
	Metadata           string `json:"metadata,omitempty"`
}

type HeartbeatManager struct {
	cfg          *config.Config
	apiClient    *api.Client
	cacheManager *cache.Manager
	stopChan     chan struct{}
}

func NewHeartbeatManager(cfg *config.Config, apiClient *api.Client, cacheManager *cache.Manager) *HeartbeatManager {
	return &HeartbeatManager{
		cfg:          cfg,
		apiClient:    apiClient,
		cacheManager: cacheManager,
		stopChan:     make(chan struct{}),
	}
}

func (m *HeartbeatManager) Start() error {
	if m.cfg.Cassandra == nil || !m.cfg.Cassandra.Enable {
		return nil
	}

	log.Println("Starting Cassandra heartbeat monitoring...")

	for _, cluster := range m.cfg.Cassandra.Clusters {
		serviceID := m.cacheManager.GetCassandraService(cluster.Service.Name)
		if serviceID == 0 {
			log.Printf("Warning: Service '%s' not found in cache, skipping monitoring\n", cluster.Service.Name)
			continue
		}

		// Start monitoring goroutine for this cluster
		go m.monitorCluster(cluster, serviceID)
	}

	return nil
}

func (m *HeartbeatManager) Stop() {
	close(m.stopChan)
}

func (m *HeartbeatManager) monitorCluster(cluster config.CassandraCluster, serviceID int64) {
	interval := time.Duration(cluster.Service.IntervalSeconds) * time.Second
	ticker := time.NewTicker(interval)
	defer ticker.Stop()

	log.Printf("Monitoring Cassandra cluster '%s' every %ds\n", cluster.Service.Name, cluster.Service.IntervalSeconds)

	// Execute immediately on start
	m.executeChecks(cluster, serviceID)

	for {
		select {
		case <-ticker.C:
			m.executeChecks(cluster, serviceID)
		case <-m.stopChan:
			return
		}
	}
}

func (m *HeartbeatManager) executeChecks(cluster config.CassandraCluster, serviceID int64) {
	// Execute per-node checks
	for _, instance := range cluster.Instances {
		instanceID := m.cacheManager.GetInstanceByHostname(instance.Hostname)
		if instanceID == 0 {
			log.Printf("Warning: Instance '%s' not found in cache, skipping\n", instance.Hostname)
			continue
		}

		cacheKey := fmt.Sprintf("%d:%d:%d", serviceID, instanceID, instance.Port)
		serviceInstanceID := m.cacheManager.GetServiceInstance(cacheKey)
		if serviceInstanceID == 0 {
			log.Printf("Warning: ServiceInstance '%s:%d' not found in cache, skipping\n", instance.Hostname, instance.Port)
			continue
		}

		connectHost := instance.IP
		if connectHost == "" {
			connectHost = instance.Hostname
		}
		success, responseTimeMs, err := ExecuteTCPCheck(connectHost, instance.Port, cluster.Service.TimeoutMs)

		status := "UP"
		errorMsg := ""
		if !success {
			status = "DOWN"
			if err != nil {
				errorMsg = err.Error()
			}
		} else if responseTimeMs > cluster.Service.LatencyCriticalMs {
			status = "CRITICAL"
		} else if responseTimeMs > cluster.Service.LatencyWarningMs {
			status = "WARNING"
		}

		heartbeat := ServiceHeartbeat{
			MonitoredServiceID: serviceID,
			ServiceInstanceID:  &serviceInstanceID,
			ExecutedAt:         time.Now().UTC().Format(time.RFC3339),
			Success:            success,
			Status:             status,
			ResponseTimeMs:     responseTimeMs,
			ErrorMessage:       errorMsg,
		}

		if err := m.submitHeartbeat(heartbeat); err != nil {
			log.Printf("Failed to submit heartbeat for %s:%d: %v\n", instance.Hostname, instance.Port, err)
		} else {
			log.Printf("✓ Cassandra heartbeat: %s:%d - %s (%dms)\n", instance.Hostname, instance.Port, status, responseTimeMs)
		}
	}

	// Submit cluster-wide heartbeat if cluster monitoring enabled
	if cluster.Service.ClusterMonitoringEnabled {
		m.submitClusterHeartbeat(cluster, serviceID)
	}
}

func (m *HeartbeatManager) submitHeartbeat(heartbeat ServiceHeartbeat) error {
	data, err := json.Marshal(heartbeat)
	if err != nil {
		return fmt.Errorf("failed to marshal heartbeat: %w", err)
	}

	resp, err := m.apiClient.Post("/api/agent/service-heartbeats", bytes.NewReader(data))
	if err != nil {
		return fmt.Errorf("failed to submit heartbeat: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != 201 && resp.StatusCode != 200 {
		return fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}

	return nil
}

func (m *HeartbeatManager) submitClusterHeartbeat(cluster config.CassandraCluster, serviceID int64) {
	// Collect basic TCP metrics
	upNodes := 0
	downNodes := 0
	totalLatency := 0
	
	for _, instance := range cluster.Instances {
		connectHost := instance.IP
		if connectHost == "" {
			connectHost = instance.Hostname
		}
		success, latency, _ := ExecuteTCPCheck(connectHost, instance.Port, cluster.Service.TimeoutMs)
		if success {
			upNodes++
			totalLatency += latency
		} else {
			downNodes++
		}
	}
	
	clusterStatus := "UP"
	if downNodes == len(cluster.Instances) {
		clusterStatus = "DOWN"
	} else if downNodes > 0 {
		clusterStatus = "DEGRADED"
	}
	
	avgLatency := 0
	if upNodes > 0 {
		avgLatency = totalLatency / upNodes
	}
	
	// Build metadata with basic health
	metadata := map[string]interface{}{
		"clusterHealth": map[string]interface{}{
			"upNodes":      upNodes,
			"downNodes":    downNodes,
			"totalNodes":   len(cluster.Instances),
			"avgLatencyMs": avgLatency,
		},
	}
	
	// Collect CQL metrics if credentials provided
	if cluster.Service.AdvancedConfig != nil {
		if cqlRaw, exists := cluster.Service.AdvancedConfig["cql"]; exists {
			var username, password string
			
			switch cql := cqlRaw.(type) {
			case map[string]interface{}:
				username, _ = cql["username"].(string)
				password, _ = cql["password"].(string)
			case map[interface{}]interface{}:
				if u, ok := cql["username"].(string); ok {
					username = u
				}
				if p, ok := cql["password"].(string); ok {
					password = p
				}
			}
			
			if username != "" && upNodes > 0 {
				for _, instance := range cluster.Instances {
					host := instance.IP
					if host == "" {
						host = instance.Hostname
					}
					
					if metrics, err := CollectMetrics(host, instance.Port, username, password); err == nil {
						metadata["cassandraMetrics"] = metrics
						break
					}
				}
			}
		}
	}
	
	metadataJSON, _ := json.Marshal(metadata)
	
	heartbeat := ServiceHeartbeat{
		MonitoredServiceID: serviceID,
		ServiceInstanceID:  nil,
		ExecutedAt:         time.Now().UTC().Format(time.RFC3339),
		Success:            clusterStatus != "DOWN",
		Status:             clusterStatus,
		ResponseTimeMs:     avgLatency,
		Metadata:           string(metadataJSON),
	}
	
	if err := m.submitHeartbeat(heartbeat); err != nil {
		log.Printf("Failed to submit cluster heartbeat: %v\n", err)
	} else {
		log.Printf("✓ Cluster heartbeat: %s - %s (%d/%d nodes up)\n", cluster.Service.Name, clusterStatus, upNodes, len(cluster.Instances))
	}
}
