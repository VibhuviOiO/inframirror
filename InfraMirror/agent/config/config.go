package config

import (
	"fmt"
	"os"
	"gopkg.in/yaml.v2"
)

type Config struct {
	APIKey       string     `yaml:"api_key"`
	Name         string     `yaml:"name"`
	Region       string     `yaml:"region"`
	Datacenter   string     `yaml:"datacenter"`
	Backend      Backend    `yaml:"backend"`
	Agent        Agent      `yaml:"agent"`
	Instance     Instance   `yaml:"instance"`
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

type Instance struct {
	Enable                        bool `yaml:"enable"`
	PingEnabled                   bool `yaml:"ping_enabled"`
	PingInterval                  int  `yaml:"ping_interval"`
	PingTimeoutMs                 int  `yaml:"ping_timeout_ms"`
	PingRetryCount                int  `yaml:"ping_retry_count"`
	HardwareMonitoringEnabled     bool `yaml:"hardware_monitoring_enabled"`
	HardwareMonitoringInterval    int  `yaml:"hardware_monitoring_interval"`
	CPUWarningThreshold           int  `yaml:"cpu_warning_threshold"`
	CPUDangerThreshold            int  `yaml:"cpu_danger_threshold"`
	MemoryWarningThreshold        int  `yaml:"memory_warning_threshold"`
	MemoryDangerThreshold         int  `yaml:"memory_danger_threshold"`
	DiskWarningThreshold          int  `yaml:"disk_warning_threshold"`
	DiskDangerThreshold           int  `yaml:"disk_danger_threshold"`
}

func LoadConfig(globalPath, instancePath string) (*Config, error) {
	// Load global config
	globalData, err := os.ReadFile(globalPath)
	if err != nil {
		return nil, fmt.Errorf("failed to read global config: %w", err)
	}

	var config Config
	if err := yaml.Unmarshal(globalData, &config); err != nil {
		return nil, fmt.Errorf("failed to parse global config: %w", err)
	}

	// Load instance config if exists
	if instancePath != "" {
		if _, err := os.Stat(instancePath); err == nil {
			instanceData, err := os.ReadFile(instancePath)
			if err != nil {
				return nil, fmt.Errorf("failed to read instance config: %w", err)
			}

			var instanceConfig Config
			if err := yaml.Unmarshal(instanceData, &instanceConfig); err != nil {
				return nil, fmt.Errorf("failed to parse instance config: %w", err)
			}

			// Merge instance config into global config
			if instanceConfig.Instance.Enable {
				config.Instance = instanceConfig.Instance
			}
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
	if config.Instance.PingInterval == 0 {
		config.Instance.PingInterval = 30
	}
	if config.Instance.PingTimeoutMs == 0 {
		config.Instance.PingTimeoutMs = 3000
	}
	if config.Instance.PingRetryCount == 0 {
		config.Instance.PingRetryCount = 2
	}
	if config.Instance.HardwareMonitoringInterval == 0 {
		config.Instance.HardwareMonitoringInterval = 300
	}
	if config.Instance.CPUWarningThreshold == 0 {
		config.Instance.CPUWarningThreshold = 70
	}
	if config.Instance.CPUDangerThreshold == 0 {
		config.Instance.CPUDangerThreshold = 90
	}
	if config.Instance.MemoryWarningThreshold == 0 {
		config.Instance.MemoryWarningThreshold = 75
	}
	if config.Instance.MemoryDangerThreshold == 0 {
		config.Instance.MemoryDangerThreshold = 90
	}
	if config.Instance.DiskWarningThreshold == 0 {
		config.Instance.DiskWarningThreshold = 80
	}
	if config.Instance.DiskDangerThreshold == 0 {
		config.Instance.DiskDangerThreshold = 95
	}

	return &config, nil
}