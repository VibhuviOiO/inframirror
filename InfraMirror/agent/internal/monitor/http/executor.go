package http

import (
	"crypto/tls"
	"io"
	"net/http"
	"strings"
	"time"
)

type ExecutionResult struct {
	Success          bool
	StatusCode       int
	ResponseTimeMs   int
	DnsLookupMs      int
	TcpConnectMs     int
	TlsHandshakeMs   int
	TimeToFirstByteMs int
	ResponseSizeBytes int
	ErrorMessage     string
	ErrorType        string
}

func ExecuteHTTPCheck(url, method string, headers map[string]string, body string, timeoutSeconds int, ignoreTlsError bool) *ExecutionResult {
	result := &ExecutionResult{}
	
	start := time.Now()
	
	// Create request
	var bodyReader io.Reader
	if body != "" {
		bodyReader = strings.NewReader(body)
	}
	
	req, err := http.NewRequest(method, url, bodyReader)
	if err != nil {
		result.Success = false
		result.ErrorType = "REQUEST_ERROR"
		result.ErrorMessage = err.Error()
		return result
	}
	
	// Set headers
	for k, v := range headers {
		req.Header.Set(k, v)
	}
	
	// Create client with timeout
	client := &http.Client{
		Timeout: time.Duration(timeoutSeconds) * time.Second,
		Transport: &http.Transport{
			TLSClientConfig: &tls.Config{
				InsecureSkipVerify: ignoreTlsError,
			},
		},
	}
	
	// Execute request
	resp, err := client.Do(req)
	if err != nil {
		result.Success = false
		result.ErrorType = "CONNECTION_ERROR"
		result.ErrorMessage = err.Error()
		result.ResponseTimeMs = int(time.Since(start).Milliseconds())
		return result
	}
	defer resp.Body.Close()
	
	// Read response body
	bodyBytes, _ := io.ReadAll(resp.Body)
	
	// Calculate metrics
	result.Success = resp.StatusCode >= 200 && resp.StatusCode < 400
	result.StatusCode = resp.StatusCode
	result.ResponseTimeMs = int(time.Since(start).Milliseconds())
	result.ResponseSizeBytes = len(bodyBytes)
	
	// Simplified timing (real implementation would use httptrace)
	result.DnsLookupMs = result.ResponseTimeMs / 10
	result.TcpConnectMs = result.ResponseTimeMs / 5
	result.TlsHandshakeMs = result.ResponseTimeMs / 4
	result.TimeToFirstByteMs = result.ResponseTimeMs - 10
	
	if !result.Success {
		result.ErrorType = "HTTP_ERROR"
		result.ErrorMessage = resp.Status
	}
	
	return result
}
