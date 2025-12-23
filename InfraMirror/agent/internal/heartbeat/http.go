package heartbeat

import (
	"log"
	"time"

	"inframirror-agent/internal/api"
	"inframirror-agent/internal/cache"
	"inframirror-agent/internal/monitor/http"
)

type HttpHeartbeatManager struct {
	client       *api.Client
	cacheManager *cache.Manager
	monitors     []HttpMonitorConfig
	stopChan     chan struct{}
}

type HttpMonitorConfig struct {
	ID                int64             `json:"id"`
	Name              string            `json:"name"`
	URL               string            `json:"url"`
	Method            string            `json:"method"`
	Interval          int               `json:"intervalSeconds"`
	TimeoutSeconds    int               `json:"timeoutSeconds"`
	IgnoreTlsError    bool              `json:"ignoreTlsError"`
	Headers           map[string]string `json:"headers"`
	Body              string            `json:"body"`
}

func NewHttpHeartbeatManager(client *api.Client, cacheManager *cache.Manager) *HttpHeartbeatManager {
	return &HttpHeartbeatManager{
		client:       client,
		cacheManager: cacheManager,
		stopChan:     make(chan struct{}),
	}
}

func (m *HttpHeartbeatManager) Start() error {
	if err := m.loadMonitors(); err != nil {
		return err
	}

	for _, monitor := range m.monitors {
		go m.monitorLoop(monitor)
	}

	log.Printf("Started HTTP monitoring for %d monitors", len(m.monitors))
	return nil
}

func (m *HttpHeartbeatManager) Stop() {
	close(m.stopChan)
}

func (m *HttpHeartbeatManager) GetType() string {
	return "http"
}

func (m *HttpHeartbeatManager) loadMonitors() error {
	log.Println("Loading HTTP monitors from cache...")
	
	cache := m.cacheManager.GetCache()
	if cache.HTTPMonitors == nil || len(cache.HTTPMonitors) == 0 {
		log.Println("No HTTP monitors found in cache")
		return nil
	}
	
	// Load monitors from backend using cached IDs
	for name, id := range cache.HTTPMonitors {
		monitor, err := m.client.GetHTTPMonitor(id)
		if err != nil {
			log.Printf("Failed to load monitor %s (ID: %d): %v", name, id, err)
			continue
		}
		
		m.monitors = append(m.monitors, HttpMonitorConfig{
			ID:             monitor.ID,
			Name:           monitor.Name,
			URL:            monitor.URL,
			Method:         monitor.Method,
			Interval:       monitor.IntervalSeconds,
			TimeoutSeconds: monitor.TimeoutSeconds,
			IgnoreTlsError: monitor.IgnoreTlsError,
			Headers:        monitor.Headers,
			Body:           monitor.Body,
		})
	}
	
	log.Printf("Loaded %d HTTP monitors", len(m.monitors))
	return nil
}

func (m *HttpHeartbeatManager) monitorLoop(monitor HttpMonitorConfig) {
	interval := time.Duration(monitor.Interval) * time.Second
	ticker := time.NewTicker(interval)
	defer ticker.Stop()

	for {
		select {
		case <-ticker.C:
			m.executeHttpCheck(monitor)
		case <-m.stopChan:
			return
		}
	}
}

func (m *HttpHeartbeatManager) executeHttpCheck(monitor HttpMonitorConfig) {
	log.Printf("Executing HTTP check: %s (%s %s)", monitor.Name, monitor.Method, monitor.URL)
	
	// Execute HTTP check
	result := http.ExecuteHTTPCheck(
		monitor.URL,
		monitor.Method,
		monitor.Headers,
		monitor.Body,
		monitor.TimeoutSeconds,
		monitor.IgnoreTlsError,
	)
	
	// Submit heartbeat
	heartbeat := &HttpHeartbeat{
		MonitorID:         monitor.ID,
		ExecutedAt:        time.Now(),
		Success:           result.Success,
		StatusCode:        result.StatusCode,
		ResponseTimeMs:    result.ResponseTimeMs,
		DnsLookupMs:       result.DnsLookupMs,
		TcpConnectMs:      result.TcpConnectMs,
		TlsHandshakeMs:    result.TlsHandshakeMs,
		TimeToFirstByteMs: result.TimeToFirstByteMs,
		ResponseSizeBytes: result.ResponseSizeBytes,
		ErrorMessage:      result.ErrorMessage,
		ErrorType:         result.ErrorType,
	}
	
	if err := m.client.SubmitHttpHeartbeat(heartbeat); err != nil {
		log.Printf("Failed to submit heartbeat for %s: %v", monitor.Name, err)
	} else {
		if result.Success {
			log.Printf("✓ %s: %d (%dms)", monitor.Name, result.StatusCode, result.ResponseTimeMs)
		} else {
			log.Printf("✗ %s: %s", monitor.Name, result.ErrorMessage)
		}
	}
}