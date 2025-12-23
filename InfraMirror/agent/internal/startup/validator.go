package startup

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"time"

	"inframirror-agent/internal/api"
	"inframirror-agent/internal/cache"
)

type HealthResponse struct {
	Status string `json:"status"`
}

type Validator struct {
	baseURL      string
	apiKey       string
	client       *http.Client
	apiClient    *api.Client
	cacheManager *cache.Manager
}

func NewValidator(baseURL, apiKey string, apiClient *api.Client, cacheManager *cache.Manager) *Validator {
	return &Validator{
		baseURL:      baseURL,
		apiKey:       apiKey,
		client:       &http.Client{Timeout: 10 * time.Second},
		apiClient:    apiClient,
		cacheManager: cacheManager,
	}
}

// CheckBackendHealth verifies backend is UP, retries indefinitely
func (v *Validator) CheckBackendHealth() {
	healthURL := v.baseURL + "/management/health"
	retryDelay := 5 * time.Second
	maxRetryDelay := 60 * time.Second

	for {
		resp, err := v.client.Get(healthURL)
		if err != nil {
			log.Printf("❌ Backend unreachable: %v. Retrying in %v...", err, retryDelay)
			time.Sleep(retryDelay)
			if retryDelay < maxRetryDelay {
				retryDelay *= 2
			}
			continue
		}
		defer resp.Body.Close()

		var health HealthResponse
		if err := json.NewDecoder(resp.Body).Decode(&health); err != nil {
			log.Printf("❌ Failed to parse health response: %v. Retrying in %v...", err, retryDelay)
			time.Sleep(retryDelay)
			continue
		}

		if health.Status != "UP" {
			log.Printf("❌ Backend status: %s. Retrying in %v...", health.Status, retryDelay)
			time.Sleep(retryDelay)
			continue
		}

		log.Println("✅ Backend health check passed")
		return
	}
}

// ValidateAPIKey tests API key by sending test heartbeat
func (v *Validator) ValidateAPIKey() error {
	log.Println("🔑 Validating API key...")
	
	err := v.apiClient.SendHeartbeat()
	if err != nil {
		return fmt.Errorf("❌ API key validation failed: %v", err)
	}

	log.Println("✅ API key is valid")
	return nil
}

// ValidateCachedResources checks if cached IDs still exist in backend
func (v *Validator) ValidateCachedResources() bool {
	if !v.cacheManager.HasCache() {
		log.Println("ℹ️  No cache found, will register agent")
		return false
	}

	cachedData := v.cacheManager.GetCache()
	log.Printf("🔍 Validating cached data - Agent: %d, Region: %d, Datacenter: %d",
		cachedData.AgentID, cachedData.RegionID, cachedData.DatacenterID)

	err := v.apiClient.SendHeartbeat()
	if err != nil {
		log.Printf("⚠️  Cached agent ID %d is invalid: %v", cachedData.AgentID, err)
		log.Println("🧹 Clearing invalid cache...")
		v.cacheManager.ClearCache()
		return false
	}

	log.Println("✅ Cached resources are valid")
	return true
}

// WaitForBackend monitors backend health continuously
func (v *Validator) WaitForBackend() {
	healthURL := v.baseURL + "/management/health"
	ticker := time.NewTicker(30 * time.Second)
	defer ticker.Stop()

	for range ticker.C {
		resp, err := v.client.Get(healthURL)
		if err != nil {
			log.Printf("⚠️  Backend connection lost: %v. Waiting for recovery...", err)
			v.CheckBackendHealth()
			log.Println("✅ Backend recovered, resuming operations")
			continue
		}
		resp.Body.Close()
	}
}
