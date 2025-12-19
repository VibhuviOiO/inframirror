package services

import (
	"fmt"
	"net"
	"strings"
	"time"
)

type RedisDetector struct{}

func NewRedisDetector() *RedisDetector {
	return &RedisDetector{}
}

func (d *RedisDetector) GetType() string {
	return "REDIS"
}

func (d *RedisDetector) GetDefaultPorts() []int {
	return []int{6379}
}

func (d *RedisDetector) Detect(host string, port int) (*ServiceInfo, error) {
	conn, err := net.DialTimeout("tcp", fmt.Sprintf("%s:%d", host, port), 2*time.Second)
	if err != nil {
		return nil, err
	}
	defer conn.Close()

	// Send PING command
	_, err = conn.Write([]byte("PING\r\n"))
	if err != nil {
		return nil, err
	}

	// Read response
	buffer := make([]byte, 1024)
	conn.SetReadDeadline(time.Now().Add(2 * time.Second))
	n, err := conn.Read(buffer)
	if err != nil {
		return nil, err
	}

	response := string(buffer[:n])
	if strings.Contains(response, "PONG") {
		return &ServiceInfo{
			Type:    "REDIS",
			Version: "Unknown", // TODO: Get version with INFO command
			Host:    host,
			Port:    port,
			Metadata: map[string]interface{}{
				"ping_response": response,
			},
		}, nil
	}

	return nil, fmt.Errorf("not a Redis service")
}