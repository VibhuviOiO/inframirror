package discovery

import (
	"fmt"
	"log"
	"net"
	"strconv"
	"strings"
	"sync"
	"time"

	"inframirror-agent/internal/services"
)

type NetworkDiscovery struct {
	serviceRegistry *services.ServiceRegistry
	cidrRange       string
	portScanTimeout time.Duration
}

type DiscoveredService struct {
	Host        string                 `json:"host"`
	Port        int                    `json:"port"`
	ServiceType string                 `json:"serviceType"`
	Version     string                 `json:"version"`
	Metadata    map[string]interface{} `json:"metadata,omitempty"`
}

func NewNetworkDiscovery(cidrRange string) *NetworkDiscovery {
	return &NetworkDiscovery{
		serviceRegistry: services.NewServiceRegistry(),
		cidrRange:       cidrRange,
		portScanTimeout: 2 * time.Second,
	}
}

func (d *NetworkDiscovery) DiscoverServices() ([]DiscoveredService, error) {
	log.Printf("Starting network discovery for range: %s", d.cidrRange)
	
	hosts, err := d.getHostsFromCIDR(d.cidrRange)
	if err != nil {
		return nil, err
	}

	var discovered []DiscoveredService
	var mu sync.Mutex
	var wg sync.WaitGroup

	// Scan each host
	for _, host := range hosts {
		wg.Add(1)
		go func(h string) {
			defer wg.Done()
			services := d.scanHost(h)
			mu.Lock()
			discovered = append(discovered, services...)
			mu.Unlock()
		}(host)
	}

	wg.Wait()
	log.Printf("Discovery completed. Found %d services", len(discovered))
	return discovered, nil
}

func (d *NetworkDiscovery) scanHost(host string) []DiscoveredService {
	var services []DiscoveredService
	
	// Get all default ports from all detectors
	portsToScan := make(map[int]bool)
	for _, detector := range d.serviceRegistry.GetAllDetectors() {
		for _, port := range detector.GetDefaultPorts() {
			portsToScan[port] = true
		}
	}

	// Scan each port
	for port := range portsToScan {
		if d.isPortOpen(host, port) {
			if serviceInfo, err := d.serviceRegistry.DetectService(host, port); err == nil && serviceInfo != nil {
				services = append(services, DiscoveredService{
					Host:        host,
					Port:        port,
					ServiceType: serviceInfo.Type,
					Version:     serviceInfo.Version,
					Metadata:    serviceInfo.Metadata,
				})
				log.Printf("Discovered %s on %s:%d", serviceInfo.Type, host, port)
			}
		}
	}

	return services
}

func (d *NetworkDiscovery) isPortOpen(host string, port int) bool {
	conn, err := net.DialTimeout("tcp", fmt.Sprintf("%s:%d", host, port), d.portScanTimeout)
	if err != nil {
		return false
	}
	conn.Close()
	return true
}

func (d *NetworkDiscovery) getHostsFromCIDR(cidr string) ([]string, error) {
	ip, ipnet, err := net.ParseCIDR(cidr)
	if err != nil {
		return nil, err
	}

	var hosts []string
	for ip := ip.Mask(ipnet.Mask); ipnet.Contains(ip); d.incrementIP(ip) {
		hosts = append(hosts, ip.String())
	}

	// Remove network and broadcast addresses
	if len(hosts) > 2 {
		return hosts[1 : len(hosts)-1], nil
	}
	return hosts, nil
}

func (d *NetworkDiscovery) incrementIP(ip net.IP) {
	for j := len(ip) - 1; j >= 0; j-- {
		ip[j]++
		if ip[j] > 0 {
			break
		}
	}
}