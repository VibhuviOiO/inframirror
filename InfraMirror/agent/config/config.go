package config

import (
	"fmt"
	"os"
	"strings"
	"time"
	"gopkg.in/yaml.v2"
)

type Config struct {
	APIKey       string     `yaml:"api_key"`
	Name         string     `yaml:"name"`
	Region       string     `yaml:"region"`
	Datacenter   string     `yaml:"datacenter"`
	Backend      Backend    `yaml:"backend"`
	Agent        Agent      `yaml:"agent"`
	API          API        `yaml:"api"`
	Instances    Instance   `yaml:"instances"`
	HTTP         *HTTPConfig `yaml:"http"`
	Cassandra    *CassandraConfig `yaml:"cassandra"`
	// Runtime fields populated after registration
	AgentID      int64      `yaml:"-"`
	RegionID     int64      `yaml:"-"`
	DatacenterID int64      `yaml:"-"`
}

type Backend struct {
	URL     string `yaml:"url"`
	Timeout string `yaml:"timeout"`
}

type Agent struct {
	HeartbeatInterval int `yaml:"heartbeat_interval"`
}

type API struct {
	URL     string        `yaml:"url"`
	Key     string        `yaml:"key"`
	Timeout time.Duration `yaml:"timeout"`
}

type HTTPConfig struct {
	Enable   bool                  `yaml:"enable"`
	Defaults HTTPDefaults         `yaml:"defaults"`
	Monitors []HTTPMonitorConfig  `yaml:"monitors"`
}

type HTTPDefaults struct {
	Type                    string            `yaml:"type"`
	Method                  string            `yaml:"method"`
	IntervalSeconds         int               `yaml:"intervalSeconds"`
	TimeoutSeconds          int               `yaml:"timeoutSeconds"`
	RetryCount              int               `yaml:"retryCount"`
	RetryDelaySeconds       int               `yaml:"retryDelaySeconds"`
	Enabled                 bool              `yaml:"enabled"`
	ExpectedStatusCodes     string            `yaml:"expectedStatusCodes"`
	IncludeResponseBody     bool              `yaml:"includeResponseBody"`
	CheckSslCertificate     bool              `yaml:"checkSslCertificate"`
	IgnoreTlsError          bool              `yaml:"ignoreTlsError"`
	CertificateExpiryDays   int               `yaml:"certificateExpiryDays"`
	CheckDnsResolution      bool              `yaml:"checkDnsResolution"`
	MaxRedirects            int               `yaml:"maxRedirects"`
	ResponseTimeWarningMs   int               `yaml:"responseTimeWarningMs"`
	ResponseTimeCriticalMs  int               `yaml:"responseTimeCriticalMs"`
	PerformanceBudgetMs     int               `yaml:"performanceBudgetMs"`
	SizeBudgetKb            int               `yaml:"sizeBudgetKb"`
	UptimeWarningPercent    float64           `yaml:"uptimeWarningPercent"`
	UptimeCriticalPercent   float64           `yaml:"uptimeCriticalPercent"`
	ResendNotificationCount int               `yaml:"resendNotificationCount"`
	UpsideDownMode          bool              `yaml:"upsideDownMode"`
	Headers                 map[string]string `yaml:"headers"`
}

type HTTPMonitorConfig struct {
	Name                    string            `yaml:"name"`
	Type                    string            `yaml:"type"`
	Method                  string            `yaml:"method"`
	URL                     string            `yaml:"url"`
	Description             string            `yaml:"description"`
	Tags                    string            `yaml:"tags"`
	Headers                 map[string]string `yaml:"headers"`
	Body                    string            `yaml:"body"`
	IntervalSeconds         int               `yaml:"intervalSeconds"`
	TimeoutSeconds          int               `yaml:"timeoutSeconds"`
	RetryCount              int               `yaml:"retryCount"`
	RetryDelaySeconds       int               `yaml:"retryDelaySeconds"`
	Enabled                 *bool             `yaml:"enabled"`
	ExpectedStatusCodes     string            `yaml:"expectedStatusCodes"`
	IncludeResponseBody     *bool             `yaml:"includeResponseBody"`
	CheckSslCertificate     *bool             `yaml:"checkSslCertificate"`
	IgnoreTlsError          *bool             `yaml:"ignoreTlsError"`
	CertificateExpiryDays   int               `yaml:"certificateExpiryDays"`
	CheckDnsResolution      *bool             `yaml:"checkDnsResolution"`
	MaxRedirects            int               `yaml:"maxRedirects"`
	ResponseTimeWarningMs   int               `yaml:"responseTimeWarningMs"`
	ResponseTimeCriticalMs  int               `yaml:"responseTimeCriticalMs"`
	PerformanceBudgetMs     int               `yaml:"performanceBudgetMs"`
	SizeBudgetKb            int               `yaml:"sizeBudgetKb"`
	UptimeWarningPercent    float64           `yaml:"uptimeWarningPercent"`
	UptimeCriticalPercent   float64           `yaml:"uptimeCriticalPercent"`
	ResendNotificationCount int               `yaml:"resendNotificationCount"`
	UpsideDownMode          *bool             `yaml:"upsideDownMode"`
}

type CassandraConfig struct {
	Enable   bool              `yaml:"enable"`
	Clusters []CassandraCluster `yaml:"clusters"`
}

type CassandraCluster struct {
	Service   CassandraService   `yaml:"service"`
	Instances []CassandraInstance `yaml:"instances"`
}

type CassandraService struct {
	Name                     string                 `yaml:"name"`
	Description              string                 `yaml:"description"`
	ServiceType              string                 `yaml:"serviceType"`
	Environment              string                 `yaml:"environment"`
	MonitoringEnabled        bool                   `yaml:"monitoringEnabled"`
	ClusterMonitoringEnabled bool                   `yaml:"clusterMonitoringEnabled"`
	IntervalSeconds          int                    `yaml:"intervalSeconds"`
	TimeoutMs                int                    `yaml:"timeoutMs"`
	RetryCount               int                    `yaml:"retryCount"`
	LatencyWarningMs         int                    `yaml:"latencyWarningMs"`
	LatencyCriticalMs        int                    `yaml:"latencyCriticalMs"`
	IsActive                 bool                   `yaml:"isActive"`
	AdvancedConfig           map[string]interface{} `yaml:"advancedConfig"`
}

type CassandraInstance struct {
	Name     string `yaml:"name"`
	Hostname string `yaml:"hostname"`
	IP       string `yaml:"ip"`
	Port     int    `yaml:"port"`
	IsActive bool   `yaml:"isActive"`
}

type Instance struct {
	Enable                        bool                `yaml:"enable"`
	PingEnabled                   bool                `yaml:"pingEnabled"`
	PingInterval                  int                 `yaml:"pingInterval"`
	PingTimeoutMs                 int                 `yaml:"pingTimeout"`
	PingRetryCount                int                 `yaml:"pingRetryCount"`
	HardwareMonitoring            HardwareMonitoring  `yaml:"hardwareMonitoring"`
	CPUWarningThreshold           int                 `yaml:"cpu_warning_threshold"`
	CPUDangerThreshold            int                 `yaml:"cpu_danger_threshold"`
	MemoryWarningThreshold        int                 `yaml:"memory_warning_threshold"`
	MemoryDangerThreshold         int                 `yaml:"memory_danger_threshold"`
	DiskWarningThreshold          int                 `yaml:"disk_warning_threshold"`
	DiskDangerThreshold           int                 `yaml:"disk_danger_threshold"`
}

type HardwareMonitoring struct {
	Enable   bool `yaml:"enable"`
	Interval int  `yaml:"interval"`
}

func LoadConfig(globalPath, configDir string) (*Config, error) {
	// Load global config
	globalData, err := os.ReadFile(globalPath)
	if err != nil {
		return nil, fmt.Errorf("failed to read global config: %w", err)
	}

	var config Config
	if err := yaml.Unmarshal(globalData, &config); err != nil {
		return nil, fmt.Errorf("failed to parse global config: %w", err)
	}

	// Load all config files from config directory (recursively)
	if configDir != "" {
		if err := loadConfigsRecursive(configDir, &config); err != nil {
			fmt.Printf("Warning: failed to load configs from %s: %v\n", configDir, err)
		}
	}

	// Set defaults
	if config.Name == "" {
		hostname, _ := os.Hostname()
		config.Name = hostname
	}

	if config.Backend.URL == "" {
		config.Backend.URL = "http://localhost:8080"
	}

	if config.Backend.Timeout == "" {
		config.Backend.Timeout = "10s"
	}

	if config.Agent.HeartbeatInterval == 0 {
		config.Agent.HeartbeatInterval = 30
	}

	// Instance defaults (matching Java entity defaults)
	if config.Instances.PingInterval == 0 {
		config.Instances.PingInterval = 30
	}
	if config.Instances.PingTimeoutMs == 0 {
		config.Instances.PingTimeoutMs = 3000
	}
	if config.Instances.PingRetryCount == 0 {
		config.Instances.PingRetryCount = 2
	}
	if config.Instances.HardwareMonitoring.Interval == 0 {
		config.Instances.HardwareMonitoring.Interval = 300
	}
	if config.Instances.CPUWarningThreshold == 0 {
		config.Instances.CPUWarningThreshold = 70
	}
	if config.Instances.CPUDangerThreshold == 0 {
		config.Instances.CPUDangerThreshold = 90
	}
	if config.Instances.MemoryWarningThreshold == 0 {
		config.Instances.MemoryWarningThreshold = 75
	}
	if config.Instances.MemoryDangerThreshold == 0 {
		config.Instances.MemoryDangerThreshold = 90
	}
	if config.Instances.DiskWarningThreshold == 0 {
		config.Instances.DiskWarningThreshold = 80
	}
	if config.Instances.DiskDangerThreshold == 0 {
		config.Instances.DiskDangerThreshold = 95
	}

	return &config, nil
}

func loadConfigsRecursive(dir string, config *Config) error {
	entries, err := os.ReadDir(dir)
	if err != nil {
		return err
	}

	for _, entry := range entries {
		path := fmt.Sprintf("%s/%s", dir, entry.Name())

		if entry.IsDir() {
			// Recursively load from subdirectories
			if err := loadConfigsRecursive(path, config); err != nil {
				fmt.Printf("Warning: failed to load configs from %s: %v\n", path, err)
			}
			continue
		}

		if !strings.HasSuffix(entry.Name(), ".yml") {
			continue
		}

		configData, err := os.ReadFile(path)
		if err != nil {
			fmt.Printf("Warning: failed to read %s: %v\n", path, err)
			continue
		}

		var moduleConfig Config
		if err := yaml.Unmarshal(configData, &moduleConfig); err != nil {
			fmt.Printf("Warning: failed to parse %s: %v\n", path, err)
			continue
		}

		// Merge module config into global config
		if moduleConfig.Instances.Enable {
			config.Instances = moduleConfig.Instances
			fmt.Printf("Loaded instance config from %s\n", path)
		}
		if moduleConfig.HTTP != nil && moduleConfig.HTTP.Enable {
			config.HTTP = moduleConfig.HTTP
			fmt.Printf("Loaded HTTP config from %s\n", path)
		}
		if moduleConfig.Cassandra != nil && moduleConfig.Cassandra.Enable {
			config.Cassandra = moduleConfig.Cassandra
			fmt.Printf("Loaded Cassandra config from %s\n", path)
		}
	}

	return nil
}
