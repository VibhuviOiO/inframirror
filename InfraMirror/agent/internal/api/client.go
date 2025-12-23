package api

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"time"
)

type Client struct {
	baseURL string
	apiKey  string
	client  *http.Client
}

type AgentRegistrationRequest struct {
	Name         string            `json:"name"`
	Hostname     string            `json:"hostname"`
	IPAddress    string            `json:"ipAddress"`
	OSType       string            `json:"osType"`
	OSVersion    string            `json:"osVersion"`
	AgentVersion string            `json:"agentVersion"`
	Tags         map[string]string `json:"tags"`
}

type AgentRegistrationResponse struct {
	AgentID    int64      `json:"agentId"`
	APIKey     string     `json:"apiKey"`
	Region     Region     `json:"region"`
	Datacenter Datacenter `json:"datacenter"`
	Status     string     `json:"status"`
}

type Region struct {
	ID   int64  `json:"id"`
	Name string `json:"name"`
}

type Datacenter struct {
	ID   int64  `json:"id"`
	Name string `json:"name"`
}

func NewClient(baseURL, apiKey string, timeout time.Duration) *Client {
	return &Client{
		baseURL: baseURL,
		apiKey:  apiKey,
		client:  &http.Client{Timeout: timeout},
	}
}

func (c *Client) RegisterAgent(req *AgentRegistrationRequest) (*AgentRegistrationResponse, error) {
	data, err := json.Marshal(req)
	if err != nil {
		return nil, err
	}

	httpReq, err := http.NewRequest("POST", c.baseURL+"/api/agent/register", bytes.NewBuffer(data))
	if err != nil {
		return nil, err
	}

	httpReq.Header.Set("Content-Type", "application/json")
	httpReq.Header.Set("X-API-Key", c.apiKey)

	resp, err := c.client.Do(httpReq)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		return nil, fmt.Errorf("registration failed with status: %d", resp.StatusCode)
	}

	var result AgentRegistrationResponse
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return nil, err
	}

	return &result, nil
}

func (c *Client) SendHeartbeat() error {
	httpReq, err := http.NewRequest("POST", c.baseURL+"/api/agent/heartbeat", nil)
	if err != nil {
		return err
	}

	httpReq.Header.Set("X-API-Key", c.apiKey)

	resp, err := c.client.Do(httpReq)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("heartbeat failed with status: %d", resp.StatusCode)
	}

	return nil
}

func (c *Client) CreateRegion(name, code, group string) (*Region, error) {
	req := map[string]string{
		"name":       name,
		"regionCode": code,
		"groupName":  group,
	}

	data, err := json.Marshal(req)
	if err != nil {
		return nil, err
	}

	httpReq, err := http.NewRequest("POST", c.baseURL+"/api/agent/regions", bytes.NewBuffer(data))
	if err != nil {
		return nil, err
	}

	httpReq.Header.Set("Content-Type", "application/json")
	httpReq.Header.Set("X-API-Key", c.apiKey)

	resp, err := c.client.Do(httpReq)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		return nil, fmt.Errorf("create region failed with status: %d", resp.StatusCode)
	}

	var result Region
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return nil, err
	}

	return &result, nil
}

func (c *Client) CreateDatacenter(name, code string, regionID int64) (*Datacenter, error) {
	req := map[string]interface{}{
		"name": name,
		"code": code,
		"region": map[string]int64{"id": regionID},
	}

	data, err := json.Marshal(req)
	if err != nil {
		return nil, err
	}

	httpReq, err := http.NewRequest("POST", c.baseURL+"/api/agent/datacenters", bytes.NewBuffer(data))
	if err != nil {
		return nil, err
	}

	httpReq.Header.Set("Content-Type", "application/json")
	httpReq.Header.Set("X-API-Key", c.apiKey)

	resp, err := c.client.Do(httpReq)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		return nil, fmt.Errorf("create datacenter failed with status: %d", resp.StatusCode)
	}

	var result Datacenter
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return nil, err
	}

	return &result, nil
}

type Instance struct {
	ID   int64  `json:"id"`
	Name string `json:"name"`
}

func (c *Client) CreateInstance(name, hostname, osType, platform, privateIP string, datacenterID, agentID int64) (*Instance, error) {
	req := map[string]interface{}{
		"name":                        name,
		"hostname":                    hostname,
		"instanceType":                "VM",
		"monitoringType":              "AGENT_MONITORED",
		"operatingSystem":             osType,
		"platform":                    platform,
		"privateIpAddress":            privateIP,
		"pingEnabled":                 true,
		"pingInterval":                30,
		"pingTimeoutMs":               3000,
		"pingRetryCount":              2,
		"hardwareMonitoringEnabled":   true,
		"hardwareMonitoringInterval":  300,
		"cpuWarningThreshold":         70,
		"cpuDangerThreshold":          90,
		"memoryWarningThreshold":      75,
		"memoryDangerThreshold":       90,
		"diskWarningThreshold":        80,
		"diskDangerThreshold":         95,
		"datacenter":                  map[string]int64{"id": datacenterID},
		"agent":                       map[string]int64{"id": agentID},
	}

	fmt.Printf("DEBUG: Creating instance with OS=%s, platform=%s\n", osType, platform)

	data, err := json.Marshal(req)
	if err != nil {
		return nil, err
	}

	httpReq, err := http.NewRequest("POST", c.baseURL+"/api/agent/instances", bytes.NewBuffer(data))
	if err != nil {
		return nil, err
	}

	httpReq.Header.Set("Content-Type", "application/json")
	httpReq.Header.Set("X-API-Key", c.apiKey)

	resp, err := c.client.Do(httpReq)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		return nil, fmt.Errorf("create instance failed with status: %d", resp.StatusCode)
	}

	var result Instance
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return nil, err
	}

	return &result, nil
}

func (c *Client) SubmitInstanceHeartbeat(heartbeat map[string]interface{}) error {
	data, err := json.Marshal(heartbeat)
	if err != nil {
		return err
	}

	httpReq, err := http.NewRequest("POST", c.baseURL+"/api/agent/instance-heartbeats", bytes.NewBuffer(data))
	if err != nil {
		return err
	}

	httpReq.Header.Set("Content-Type", "application/json")
	httpReq.Header.Set("X-API-Key", c.apiKey)

	resp, err := c.client.Do(httpReq)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("submit heartbeat failed with status: %d", resp.StatusCode)
	}

	return nil
}

type HTTPMonitor struct {
	ID                int64             `json:"id"`
	Name              string            `json:"name"`
	URL               string            `json:"url"`
	Method            string            `json:"method"`
	IntervalSeconds   int               `json:"intervalSeconds"`
	TimeoutSeconds    int               `json:"timeoutSeconds"`
	IgnoreTlsError    bool              `json:"ignoreTlsError"`
	Headers           map[string]string `json:"headers"`
	Body              string            `json:"body"`
}

func (c *Client) GetHTTPMonitor(id int64) (*HTTPMonitor, error) {
	httpReq, err := http.NewRequest("GET", fmt.Sprintf("%s/api/agent/http-monitors/%d", c.baseURL, id), nil)
	if err != nil {
		return nil, err
	}

	httpReq.Header.Set("X-API-Key", c.apiKey)

	resp, err := c.client.Do(httpReq)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("get monitor failed with status: %d", resp.StatusCode)
	}

	var result HTTPMonitor
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return nil, err
	}

	return &result, nil
}

func (c *Client) SubmitHttpHeartbeat(heartbeat interface{}) error {
	data, err := json.Marshal(heartbeat)
	if err != nil {
		return err
	}

	httpReq, err := http.NewRequest("POST", c.baseURL+"/api/agent/http-heartbeats", bytes.NewBuffer(data))
	if err != nil {
		return err
	}

	httpReq.Header.Set("Content-Type", "application/json")
	httpReq.Header.Set("X-API-Key", c.apiKey)

	resp, err := c.client.Do(httpReq)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated && resp.StatusCode != http.StatusOK {
		return fmt.Errorf("submit http heartbeat failed with status: %d", resp.StatusCode)
	}

	return nil
}

func (c *Client) Post(path string, body *bytes.Reader) (*http.Response, error) {
	httpReq, err := http.NewRequest("POST", c.baseURL+path, body)
	if err != nil {
		return nil, err
	}

	httpReq.Header.Set("Content-Type", "application/json")
	httpReq.Header.Set("X-API-Key", c.apiKey)

	return c.client.Do(httpReq)
}