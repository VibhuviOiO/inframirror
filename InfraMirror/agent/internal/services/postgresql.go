package services

import (
	"fmt"
	"net"
	"time"
)

type PostgreSQLDetector struct{}

func NewPostgreSQLDetector() *PostgreSQLDetector {
	return &PostgreSQLDetector{}
}

func (d *PostgreSQLDetector) GetType() string {
	return "POSTGRESQL"
}

func (d *PostgreSQLDetector) GetDefaultPorts() []int {
	return []int{5432}
}

func (d *PostgreSQLDetector) Detect(host string, port int) (*ServiceInfo, error) {
	conn, err := net.DialTimeout("tcp", fmt.Sprintf("%s:%d", host, port), 2*time.Second)
	if err != nil {
		return nil, err
	}
	defer conn.Close()

	// Send PostgreSQL startup message
	startupMsg := []byte{
		0x00, 0x00, 0x00, 0x08, // Length
		0x04, 0xd2, 0x16, 0x2f, // Protocol version
	}
	
	_, err = conn.Write(startupMsg)
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

	// Check for PostgreSQL error response (starts with 'E')
	if n > 0 && buffer[0] == 'E' {
		return &ServiceInfo{
			Type:    "POSTGRESQL",
			Version: "Unknown",
			Host:    host,
			Port:    port,
			Metadata: map[string]interface{}{
				"detection_method": "protocol_handshake",
			},
		}, nil
	}

	return nil, fmt.Errorf("not a PostgreSQL service")
}