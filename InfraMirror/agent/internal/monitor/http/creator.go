package http

import (
	"bytes"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strings"

	"inframirror-agent/config"
	"inframirror-agent/internal/cache"
)

type HTTPMonitor struct {
	Name                     string            `json:"name"`
	Type                     string            `json:"type"`
	Method                   string            `json:"method"`
	URL                      string            `json:"url"`
	Description              string            `json:"description,omitempty"`
	Tags                     string            `json:"tags,omitempty"`
	Headers                  map[string]string `json:"headers,omitempty"`
	Body                     string            `json:"body,omitempty"`
	IntervalSeconds          int               `json:"intervalSeconds"`
	TimeoutSeconds           int               `json:"timeoutSeconds"`
	RetryCount               int               `json:"retryCount"`
	RetryDelaySeconds        int               `json:"retryDelaySeconds"`
	Enabled                  bool              `json:"enabled"`
	ExpectedStatusCodes      string            `json:"expectedStatusCodes,omitempty"`
	IncludeResponseBody      bool              `json:"includeResponseBody"`
	CheckSslCertificate      bool              `json:"checkSslCertificate"`
	IgnoreTlsError           bool              `json:"ignoreTlsError"`
	CertificateExpiryDays    int               `json:"certificateExpiryDays,omitempty"`
	CheckDnsResolution       bool              `json:"checkDnsResolution"`
	MaxRedirects             int               `json:"maxRedirects,omitempty"`
	ResponseTimeWarningMs    int               `json:"responseTimeWarningMs,omitempty"`
	ResponseTimeCriticalMs   int               `json:"responseTimeCriticalMs,omitempty"`
	PerformanceBudgetMs      int               `json:"performanceBudgetMs,omitempty"`
	SizeBudgetKb             int               `json:"sizeBudgetKb,omitempty"`
	UptimeWarningPercent     float64           `json:"uptimeWarningPercent,omitempty"`
	UptimeCriticalPercent    float64           `json:"uptimeCriticalPercent,omitempty"`
	ResendNotificationCount  int               `json:"resendNotificationCount,omitempty"`
	UpsideDownMode           bool              `json:"upsideDownMode"`
}

type HTTPMonitorResponse struct {
	ID int64 `json:"id"`
}

func CreateHTTPMonitors(cfg *config.Config, cacheManager *cache.Manager) error {
	if cfg.HTTP == nil || !cfg.HTTP.Enable {
		log.Println("HTTP monitoring disabled, skipping")
		return nil
	}

	log.Printf("Creating %d HTTP monitors...", len(cfg.HTTP.Monitors))

	for _, monitor := range cfg.HTTP.Monitors {
		// Skip disabled monitors
		if monitor.Enabled != nil && !*monitor.Enabled {
			log.Printf("⊘ Skipping disabled monitor: %s", monitor.Name)
			continue
		}

		// Check if monitor already exists in cache
		if cachedID, exists := cacheManager.GetHTTPMonitor(monitor.Name); exists {
			log.Printf("✓ HTTP monitor '%s' already exists (ID: %d), skipping", monitor.Name, cachedID)
			continue
		}

		httpMonitor := applyDefaults(monitor, cfg.HTTP.Defaults)

		monitorID, err := createMonitor(cfg, httpMonitor)
		if err != nil {
			log.Printf("Failed to create HTTP monitor '%s': %v", httpMonitor.Name, err)
			continue
		}

		cacheManager.SetHTTPMonitor(httpMonitor.Name, monitorID)
		log.Printf("✓ Created HTTP monitor: %s (ID: %d)", httpMonitor.Name, monitorID)
	}

	return nil
}

func applyDefaults(monitor config.HTTPMonitorConfig, defaults config.HTTPDefaults) HTTPMonitor {
	m := HTTPMonitor{
		Name:        monitor.Name,
		Description: monitor.Description,
		Tags:        monitor.Tags,
		URL:         monitor.URL,
	}

	m.Type = getStr(monitor.Type, defaults.Type)
	m.Method = getStr(monitor.Method, defaults.Method)
	m.Body = monitor.Body

	m.Headers = make(map[string]string)
	for k, v := range defaults.Headers {
		m.Headers[k] = v
	}
	for k, v := range monitor.Headers {
		m.Headers[k] = v
	}

	m.IntervalSeconds = getInt(monitor.IntervalSeconds, defaults.IntervalSeconds)
	m.TimeoutSeconds = getInt(monitor.TimeoutSeconds, defaults.TimeoutSeconds)
	m.RetryCount = getInt(monitor.RetryCount, defaults.RetryCount)
	m.RetryDelaySeconds = getInt(monitor.RetryDelaySeconds, defaults.RetryDelaySeconds)
	m.Enabled = getBool(monitor.Enabled, defaults.Enabled)
	m.ExpectedStatusCodes = getStr(monitor.ExpectedStatusCodes, defaults.ExpectedStatusCodes)
	m.IncludeResponseBody = getBool(monitor.IncludeResponseBody, defaults.IncludeResponseBody)
	m.CheckSslCertificate = getBool(monitor.CheckSslCertificate, defaults.CheckSslCertificate)
	m.IgnoreTlsError = getBool(monitor.IgnoreTlsError, defaults.IgnoreTlsError)
	m.CertificateExpiryDays = getInt(monitor.CertificateExpiryDays, defaults.CertificateExpiryDays)
	m.CheckDnsResolution = getBool(monitor.CheckDnsResolution, defaults.CheckDnsResolution)
	m.MaxRedirects = getInt(monitor.MaxRedirects, defaults.MaxRedirects)
	m.ResponseTimeWarningMs = getInt(monitor.ResponseTimeWarningMs, defaults.ResponseTimeWarningMs)
	m.ResponseTimeCriticalMs = getInt(monitor.ResponseTimeCriticalMs, defaults.ResponseTimeCriticalMs)
	m.PerformanceBudgetMs = getInt(monitor.PerformanceBudgetMs, defaults.PerformanceBudgetMs)
	m.SizeBudgetKb = getInt(monitor.SizeBudgetKb, defaults.SizeBudgetKb)
	m.UptimeWarningPercent = getFloat(monitor.UptimeWarningPercent, defaults.UptimeWarningPercent)
	m.UptimeCriticalPercent = getFloat(monitor.UptimeCriticalPercent, defaults.UptimeCriticalPercent)
	m.ResendNotificationCount = getInt(monitor.ResendNotificationCount, defaults.ResendNotificationCount)
	m.UpsideDownMode = getBool(monitor.UpsideDownMode, defaults.UpsideDownMode)

	return m
}

func createMonitor(cfg *config.Config, monitor HTTPMonitor) (int64, error) {
	url := fmt.Sprintf("%s/api/agent/http-monitors", strings.TrimSuffix(cfg.API.URL, "/"))

	jsonData, err := json.Marshal(monitor)
	if err != nil {
		return 0, fmt.Errorf("marshal error: %w", err)
	}

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return 0, fmt.Errorf("request creation error: %w", err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("X-API-Key", cfg.API.Key)

	client := &http.Client{Timeout: cfg.API.Timeout}
	resp, err := client.Do(req)
	if err != nil {
		return 0, fmt.Errorf("request error: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		return 0, fmt.Errorf("unexpected status: %d", resp.StatusCode)
	}

	var result HTTPMonitorResponse
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return 0, fmt.Errorf("decode error: %w", err)
	}

	return result.ID, nil
}

func getInt(val, def int) int {
	if val != 0 {
		return val
	}
	return def
}

func getStr(val, def string) string {
	if val != "" {
		return val
	}
	return def
}

func getBool(val *bool, def bool) bool {
	if val != nil {
		return *val
	}
	return def
}

func getFloat(val, def float64) float64 {
	if val != 0 {
		return val
	}
	return def
}
