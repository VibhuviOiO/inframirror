package cassandra

import (
	"fmt"
	"net"
	"time"
)

// ExecuteTCPCheck performs a basic TCP connectivity check to Cassandra
func ExecuteTCPCheck(hostname string, port int, timeoutMs int) (bool, int, error) {
	address := fmt.Sprintf("%s:%d", hostname, port)
	timeout := time.Duration(timeoutMs) * time.Millisecond
	
	start := time.Now()
	conn, err := net.DialTimeout("tcp", address, timeout)
	elapsed := time.Since(start).Milliseconds()
	
	if err != nil {
		return false, int(elapsed), err
	}
	defer conn.Close()
	
	return true, int(elapsed), nil
}
