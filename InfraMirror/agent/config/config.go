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
	Instances    Instance   `yaml:"instances"`
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
			if instanceConfig.Instances.Enable {
				config.Instances = instanceConfig.Instances
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