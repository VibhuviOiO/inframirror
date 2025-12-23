package cassandra

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"inframirror-agent/config"
	"inframirror-agent/internal/api"
	"inframirror-agent/internal/cache"
	"log"
)

type MonitoredServiceRequest struct {
	Name                     string `json:"name"`
	Description              string `json:"description,omitempty"`
	ServiceType              string `json:"serviceType"`
	Environment              string `json:"environment"`
	MonitoringEnabled        bool   `json:"monitoringEnabled"`
	ClusterMonitoringEnabled bool   `json:"clusterMonitoringEnabled"`
	IntervalSeconds          int    `json:"intervalSeconds"`
	TimeoutMs                int    `json:"timeoutMs"`
	RetryCount               int    `json:"retryCount"`
	LatencyWarningMs         int    `json:"latencyWarningMs,omitempty"`
	LatencyCriticalMs        int    `json:"latencyCriticalMs,omitempty"`
	AdvancedConfig           string `json:"advancedConfig,omitempty"`
	IsActive                 bool   `json:"isActive"`
}

type MonitoredServiceResponse struct {
	ID                       int64  `json:"id"`
	Name                     string `json:"name"`
	Description              string `json:"description"`
	ServiceType              string `json:"serviceType"`
	Environment              string `json:"environment"`
	MonitoringEnabled        bool   `json:"monitoringEnabled"`
	ClusterMonitoringEnabled bool   `json:"clusterMonitoringEnabled"`
	IntervalSeconds          int    `json:"intervalSeconds"`
	TimeoutMs                int    `json:"timeoutMs"`
	RetryCount               int    `json:"retryCount"`
	LatencyWarningMs         int    `json:"latencyWarningMs"`
	LatencyCriticalMs        int    `json:"latencyCriticalMs"`
	AdvancedConfig           string `json:"advancedConfig"`
	IsActive                 bool   `json:"isActive"`
}

type ServiceInstanceRequest struct {
	MonitoredServiceID int64 `json:"monitoredServiceId"`
	InstanceID         int64 `json:"instanceId"`
	Port               int   `json:"port"`
	IsActive           bool  `json:"isActive"`
}

type ServiceInstanceResponse struct {
	ID                 int64 `json:"id"`
	MonitoredServiceID int64 `json:"monitoredServiceId"`
	InstanceID         int64 `json:"instanceId"`
	Port               int   `json:"port"`
	IsActive           bool  `json:"isActive"`
}

func CreateCassandraServices(cfg *config.Config, apiClient *api.Client, cacheManager *cache.Manager) error {
	if cfg.Cassandra == nil || !cfg.Cassandra.Enable {
		log.Println("Cassandra monitoring disabled, skipping service creation")
		return nil
	}

	log.Printf("Creating %d Cassandra cluster(s)...\n", len(cfg.Cassandra.Clusters))

	for i, cluster := range cfg.Cassandra.Clusters {
		log.Printf("Processing cluster %d: %s\n", i+1, cluster.Service.Name)

		// Check cache first
		if cachedID := cacheManager.GetCassandraService(cluster.Service.Name); cachedID > 0 {
			log.Printf("  Service '%s' already exists (ID: %d), skipping creation\n", cluster.Service.Name, cachedID)
			
			// Create service instances for cached service
			if err := createServiceInstances(cluster, cachedID, apiClient, cacheManager); err != nil {
				log.Printf("  Warning: failed to create service instances: %v\n", err)
			}
			continue
		}

		// Convert advancedConfig to JSON string
		advancedConfigJSON := []byte("{}")
		if cluster.Service.AdvancedConfig != nil {
			converted := convertMapStringInterface(cluster.Service.AdvancedConfig)
			if jsonBytes, err := json.Marshal(converted); err == nil {
				advancedConfigJSON = jsonBytes
			}
		}

		// Create MonitoredService
		serviceReq := MonitoredServiceRequest{
			Name:                     cluster.Service.Name,
			Description:              cluster.Service.Description,
			ServiceType:              cluster.Service.ServiceType,
			Environment:              cluster.Service.Environment,
			MonitoringEnabled:        cluster.Service.MonitoringEnabled,
			ClusterMonitoringEnabled: cluster.Service.ClusterMonitoringEnabled,
			IntervalSeconds:          cluster.Service.IntervalSeconds,
			TimeoutMs:                cluster.Service.TimeoutMs,
			RetryCount:               cluster.Service.RetryCount,
			LatencyWarningMs:         cluster.Service.LatencyWarningMs,
			LatencyCriticalMs:        cluster.Service.LatencyCriticalMs,
			AdvancedConfig:           string(advancedConfigJSON),
			IsActive:                 cluster.Service.IsActive,
		}

		serviceID, err := createMonitoredService(serviceReq, apiClient, cacheManager)
		if err != nil {
			log.Printf("  Error: failed to create service: %v\n", err)
			continue
		}

		log.Printf("  ✓ Created service '%s' (ID: %d)\n", cluster.Service.Name, serviceID)
		cacheManager.SetCassandraService(cluster.Service.Name, serviceID)

		// Create service instances
		if err := createServiceInstances(cluster, serviceID, apiClient, cacheManager); err != nil {
			log.Printf("  Warning: failed to create service instances: %v\n", err)
		}
	}

	log.Println("Cassandra service creation completed")
	return nil
}

func createMonitoredService(req MonitoredServiceRequest, apiClient *api.Client, cacheManager *cache.Manager) (int64, error) {
	// Build full request with datacenter
	cache := cacheManager.GetCache()
	fullReq := map[string]interface{}{
		"name":                     req.Name,
		"description":              req.Description,
		"serviceType":              req.ServiceType,
		"environment":              req.Environment,
		"monitoringEnabled":        req.MonitoringEnabled,
		"clusterMonitoringEnabled": req.ClusterMonitoringEnabled,
		"intervalSeconds":          req.IntervalSeconds,
		"timeoutMs":                req.TimeoutMs,
		"retryCount":               req.RetryCount,
		"latencyWarningMs":         req.LatencyWarningMs,
		"latencyCriticalMs":        req.LatencyCriticalMs,
		"advancedConfig":           req.AdvancedConfig,
		"isActive":                 req.IsActive,
		"datacenter":               map[string]interface{}{"id": cache.DatacenterID},
	}
	
	reqBody, err := json.Marshal(fullReq)
	if err != nil {
		return 0, fmt.Errorf("failed to marshal request: %w", err)
	}
	
	log.Printf("    Debug: Creating service with payload: %s\n", string(reqBody))

	resp, err := apiClient.Post("/api/agent/monitored-services", bytes.NewReader(reqBody))
	if err != nil {
		return 0, fmt.Errorf("failed to create service: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != 201 && resp.StatusCode != 200 {
		body, _ := io.ReadAll(resp.Body)
		log.Printf("    Debug: Service creation failed with status %d\n", resp.StatusCode)
		log.Printf("    Debug: Response body: %s\n", string(body))
		return 0, fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}

	var serviceResp MonitoredServiceResponse
	if err := json.NewDecoder(resp.Body).Decode(&serviceResp); err != nil {
		return 0, fmt.Errorf("failed to decode response: %w", err)
	}

	return serviceResp.ID, nil
}

func createServiceInstances(cluster config.CassandraCluster, serviceID int64, apiClient *api.Client, cacheManager *cache.Manager) error {
	log.Printf("  Creating %d service instance(s)...\n", len(cluster.Instances))

	for _, instance := range cluster.Instances {
		// Get instance ID from cache by hostname
		instanceID := cacheManager.GetInstanceByHostname(instance.Hostname)
		if instanceID == 0 {
			log.Printf("    Instance '%s' not found, creating...\n", instance.Hostname)
			
			// Create the instance
			createdID, err := createInstance(instance.Name, instance.Hostname, apiClient, cacheManager)
			if err != nil {
				log.Printf("    Warning: failed to create instance '%s': %v\n", instance.Hostname, err)
				continue
			}
			instanceID = createdID
			log.Printf("    ✓ Created instance '%s' (ID: %d)\n", instance.Hostname, instanceID)
		}

		// Check if service instance already exists
		cacheKey := fmt.Sprintf("%d:%d:%d", serviceID, instanceID, instance.Port)
		if cachedID := cacheManager.GetServiceInstance(cacheKey); cachedID > 0 {
			log.Printf("    Service instance '%s:%d' already exists (ID: %d), skipping\n", instance.Hostname, instance.Port, cachedID)
			continue
		}

		// Build full request with nested objects
		fullReq := map[string]interface{}{
			"port":     instance.Port,
			"isActive": instance.IsActive,
			"instance": map[string]interface{}{
				"id": instanceID,
			},
			"monitoredService": map[string]interface{}{
				"id": serviceID,
			},
		}

		instanceReqBody, err := json.Marshal(fullReq)
		if err != nil {
			log.Printf("    Warning: failed to marshal instance request: %v\n", err)
			continue
		}
		
		log.Printf("    Creating ServiceInstance for %s:%d (service=%d, instance=%d)\n", instance.Hostname, instance.Port, serviceID, instanceID)

		resp, err := apiClient.Post("/api/agent/service-instances", bytes.NewReader(instanceReqBody))
		if err != nil {
			log.Printf("    Warning: failed to create service instance: %v\n", err)
			continue
		}
		defer resp.Body.Close()

		if resp.StatusCode != 201 {
			log.Printf("    Debug: ServiceInstance request: %s\n", string(instanceReqBody))
			errorBody := make([]byte, 1024)
			n, _ := resp.Body.Read(errorBody)
			log.Printf("    Debug: ServiceInstance response: %s\n", string(errorBody[:n]))
			log.Printf("    Warning: unexpected status code: %d\n", resp.StatusCode)
			continue
		}

		var instanceResp ServiceInstanceResponse
		if err := json.NewDecoder(resp.Body).Decode(&instanceResp); err != nil {
			log.Printf("    Warning: failed to decode response: %v\n", err)
			continue
		}

		log.Printf("    ✓ Created service instance '%s:%d' (ID: %d)\n", instance.Hostname, instance.Port, instanceResp.ID)
		cacheManager.SetServiceInstance(cacheKey, instanceResp.ID)
	}

	return nil
}

func createInstance(name, hostname string, apiClient *api.Client, cacheManager *cache.Manager) (int64, error) {
	cache := cacheManager.GetCache()
	
	instanceReq := map[string]interface{}{
		"name":                      name,
		"hostname":                  hostname,
		"description":               fmt.Sprintf("Auto-created for Cassandra service on %s", hostname),
		"instanceType":              "CONTAINER",
		"monitoringType":            "AGENT_MONITORED",
		"operatingSystem":           "LINUX",
		"platform":                  "Docker",
		"pingEnabled":               true,
		"pingInterval":              30,
		"pingTimeoutMs":             3000,
		"pingRetryCount":            2,
		"hardwareMonitoringEnabled": false,
		"hardwareMonitoringInterval": 300,
		"cpuWarningThreshold":       70,
		"cpuDangerThreshold":        90,
		"memoryWarningThreshold":    75,
		"memoryDangerThreshold":     90,
		"diskWarningThreshold":      80,
		"diskDangerThreshold":       95,
		"agent":                     map[string]interface{}{"id": cache.AgentID},
		"datacenter":                map[string]interface{}{"id": cache.DatacenterID},
	}
	
	reqBody, err := json.Marshal(instanceReq)
	if err != nil {
		return 0, fmt.Errorf("failed to marshal request: %w", err)
	}
	
	resp, err := apiClient.Post("/api/agent/instances", bytes.NewReader(reqBody))
	if err != nil {
		return 0, fmt.Errorf("failed to create instance: %w", err)
	}
	defer resp.Body.Close()
	
	if resp.StatusCode != 201 {
		// Read error response
		body, _ := json.Marshal(instanceReq)
		log.Printf("    Debug: Request payload: %s\n", string(body))
		errorBody := make([]byte, 1024)
		n, _ := resp.Body.Read(errorBody)
		log.Printf("    Debug: Response: %s\n", string(errorBody[:n]))
		return 0, fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}
	
	var instanceResp struct {
		ID int64 `json:"id"`
	}
	if err := json.NewDecoder(resp.Body).Decode(&instanceResp); err != nil {
		return 0, fmt.Errorf("failed to decode response: %w", err)
	}
	
	cacheManager.SetInstanceByHostname(hostname, instanceResp.ID)
	return instanceResp.ID, nil
}

// convertMapStringInterface recursively converts map[interface{}]interface{} to map[string]interface{}
func convertMapStringInterface(m map[string]interface{}) map[string]interface{} {
	result := make(map[string]interface{})
	for k, v := range m {
		switch val := v.(type) {
		case map[interface{}]interface{}:
			converted := make(map[string]interface{})
			for mk, mv := range val {
				if strKey, ok := mk.(string); ok {
					converted[strKey] = convertValue(mv)
				}
			}
			result[k] = converted
		case map[string]interface{}:
			result[k] = convertMapStringInterface(val)
		case []interface{}:
			converted := make([]interface{}, len(val))
			for i, item := range val {
				converted[i] = convertValue(item)
			}
			result[k] = converted
		default:
			result[k] = v
		}
	}
	return result
}

func convertValue(v interface{}) interface{} {
	switch val := v.(type) {
	case map[interface{}]interface{}:
		converted := make(map[string]interface{})
		for mk, mv := range val {
			if strKey, ok := mk.(string); ok {
				converted[strKey] = convertValue(mv)
			}
		}
		return converted
	case map[string]interface{}:
		return convertMapStringInterface(val)
	case []interface{}:
		converted := make([]interface{}, len(val))
		for i, item := range val {
			converted[i] = convertValue(item)
		}
		return converted
	default:
		return v
	}
}
