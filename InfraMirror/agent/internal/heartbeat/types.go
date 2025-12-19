package heartbeat

import (
	"time"
)

// HeartbeatManager interface for all heartbeat types
type HeartbeatManager interface {
	Start() error
	Stop()
	GetType() string
}

// BaseHeartbeat contains common heartbeat fields
type BaseHeartbeat struct {
	ExecutedAt time.Time `json:"executedAt"`
	Success    bool      `json:"success"`
	AgentID    int64     `json:"agentId"`
}

// HeartbeatBatch for bulk submission
type HeartbeatBatch struct {
	AgentID            int64                    `json:"agentId"`
	Timestamp          time.Time                `json:"timestamp"`
	PingHeartbeats     []PingHeartbeat         `json:"pingHeartbeats,omitempty"`
	HardwareHeartbeats []HardwareHeartbeat     `json:"hardwareHeartbeats,omitempty"`
	ServiceHeartbeats  []ServiceHeartbeat      `json:"serviceHeartbeats,omitempty"`
	HttpHeartbeats     []HttpHeartbeat         `json:"httpHeartbeats,omitempty"`
}

// Specific heartbeat types (matching Java InstanceHeartbeat entity)
type PingHeartbeat struct {
	BaseHeartbeat
	InstanceID     int64   `json:"instanceId"`
	HeartbeatType  string  `json:"heartbeatType"` // "PING"
	ResponseTimeMs int     `json:"responseTimeMs"`
	PacketLoss     float64 `json:"packetLoss"`
	JitterMs       int     `json:"jitterMs"`
	Status         string  `json:"status"` // UP, DOWN, WARNING, etc.
	ErrorMessage   string  `json:"errorMessage,omitempty"`
	ErrorType      string  `json:"errorType,omitempty"`
}

type HardwareHeartbeat struct {
	BaseHeartbeat
	InstanceID      int64   `json:"instanceId"`
	HeartbeatType   string  `json:"heartbeatType"` // "HARDWARE"
	CPUUsage        float64 `json:"cpuUsage"`
	MemoryUsage     float64 `json:"memoryUsage"`
	DiskUsage       float64 `json:"diskUsage"`
	LoadAverage     float64 `json:"loadAverage"`
	ProcessCount    int     `json:"processCount"`
	NetworkRxBytes  int64   `json:"networkRxBytes"`
	NetworkTxBytes  int64   `json:"networkTxBytes"`
	UptimeSeconds   int64   `json:"uptimeSeconds"`
	Status          string  `json:"status"`
	ErrorMessage    string  `json:"errorMessage,omitempty"`
	ErrorType       string  `json:"errorType,omitempty"`
	Metadata        string  `json:"metadata,omitempty"`
}

type ServiceHeartbeat struct {
	BaseHeartbeat
	ServiceID         int64                  `json:"serviceId"`
	ServiceInstanceID int64                  `json:"serviceInstanceId"`
	Status            string                 `json:"status"`
	ResponseTimeMs    int                    `json:"responseTimeMs"`
	Metadata          map[string]interface{} `json:"metadata,omitempty"`
}

type HttpHeartbeat struct {
	BaseHeartbeat
	MonitorID      int64 `json:"monitorId"`
	StatusCode     int   `json:"statusCode"`
	ResponseTimeMs int   `json:"responseTimeMs"`
	DNSLookupMs    int   `json:"dnsLookupMs"`
	TCPConnectMs   int   `json:"tcpConnectMs"`
	TLSHandshakeMs int   `json:"tlsHandshakeMs"`
}