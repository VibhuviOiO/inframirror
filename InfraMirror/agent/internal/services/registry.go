package services

// ServiceDetector interface for all service types
type ServiceDetector interface {
	Detect(host string, port int) (*ServiceInfo, error)
	GetType() string
	GetDefaultPorts() []int
}

// ServiceInfo contains detected service information
type ServiceInfo struct {
	Type     string                 `json:"type"`
	Version  string                 `json:"version"`
	Host     string                 `json:"host"`
	Port     int                    `json:"port"`
	Metadata map[string]interface{} `json:"metadata,omitempty"`
}

// ServiceRegistry manages all service detectors
type ServiceRegistry struct {
	detectors map[string]ServiceDetector
}

func NewServiceRegistry() *ServiceRegistry {
	registry := &ServiceRegistry{
		detectors: make(map[string]ServiceDetector),
	}
	
	// Register all service detectors
	registry.Register(NewRedisDetector())
	registry.Register(NewPostgreSQLDetector())
	registry.Register(NewMongoDBDetector())
	registry.Register(NewElasticsearchDetector())
	registry.Register(NewCassandraDetector())
	registry.Register(NewKafkaDetector())
	
	return registry
}

func (r *ServiceRegistry) Register(detector ServiceDetector) {
	r.detectors[detector.GetType()] = detector
}

func (r *ServiceRegistry) GetDetector(serviceType string) ServiceDetector {
	return r.detectors[serviceType]
}

func (r *ServiceRegistry) GetAllDetectors() map[string]ServiceDetector {
	return r.detectors
}

func (r *ServiceRegistry) DetectService(host string, port int) (*ServiceInfo, error) {
	for _, detector := range r.detectors {
		if info, err := detector.Detect(host, port); err == nil && info != nil {
			return info, nil
		}
	}
	return nil, nil // No service detected
}