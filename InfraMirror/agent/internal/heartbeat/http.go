package heartbeat

import (
	"log"
	"time"

	"inframirror-agent/internal/api"
	"inframirror-agent/internal/cache"
)

type HttpHeartbeatManager struct {
	client       *api.Client
	cacheManager *cache.Manager
	monitors     []HttpMonitorConfig
	stopChan     chan struct{}
}

type HttpMonitorConfig struct {
	ID       int64  `json:"id"`
	Name     string `json:"name"`
	URL      string `json:"url"`
	Method   string `json:"method"`
	Interval int    `json:"intervalSeconds"`
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
	// TODO: Load assigned HTTP monitors from backend
	// GET /api/agents/{agentId}/http-monitors
	log.Println("Loading HTTP monitors from backend...")
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
	// TODO: Implement actual HTTP check and batch submission
	log.Printf("HTTP check for monitor %s (%s)", monitor.Name, monitor.URL)
}