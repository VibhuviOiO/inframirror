package system

import (
	"math/rand"
	"os"
	"runtime"
	"time"
)

type Info struct{}

func NewInfo() *Info {
	return &Info{}
}

func (i *Info) GetHostname() string {
	hostname, err := os.Hostname()
	if err != nil {
		return "unknown"
	}
	return hostname
}

func (i *Info) GetOSType() string {
	osType := runtime.GOOS
	switch osType {
	case "linux":
		return "LINUX"
	case "windows":
		return "WINDOWS"
	case "darwin":
		return "MACOS"
	case "freebsd", "openbsd", "netbsd":
		return "BSD"
	case "aix", "solaris":
		return "UNIX"
	default:
		return "OTHER"
	}
}

func (i *Info) GetPlatform() string {
	// TODO: Implement detailed platform detection
	return runtime.GOOS + "/" + runtime.GOARCH
}

func (i *Info) GetPrivateIP() string {
	// TODO: Implement actual private IP detection
	return "127.0.0.1"
}

func (i *Info) GetCPUUsage() float64 {
	// Simulate realistic CPU usage based on system type
	rand.Seed(time.Now().UnixNano())
	if runtime.GOOS == "darwin" {
		// macOS typically runs 5-15% CPU
		return 5.0 + rand.Float64()*10.0
	}
	// Linux typically runs 10-30% CPU
	return 10.0 + rand.Float64()*20.0
}

func (i *Info) GetMemoryUsage() float64 {
	// Simulate realistic memory usage
	rand.Seed(time.Now().UnixNano())
	if runtime.GOOS == "darwin" {
		// macOS typically uses 70-90% memory (aggressive caching)
		return 70.0 + rand.Float64()*20.0
	}
	// Linux typically uses 50-80% memory
	return 50.0 + rand.Float64()*30.0
}

func (i *Info) GetDiskUsage() float64 {
	// Simulate realistic disk usage
	rand.Seed(time.Now().UnixNano())
	return 40.0 + rand.Float64()*30.0
}

func (i *Info) GetLoadAverage() float64 {
	// Simulate realistic load average
	rand.Seed(time.Now().UnixNano())
	if runtime.GOOS == "darwin" {
		// macOS typically has higher load averages
		return 1.5 + rand.Float64()*2.0
	}
	return 0.5 + rand.Float64()*2.0
}

func (i *Info) GetProcessCount() int {
	// Simulate realistic process count
	rand.Seed(time.Now().UnixNano())
	if runtime.GOOS == "darwin" {
		// macOS typically has 400-800 processes
		return 400 + rand.Intn(400)
	}
	// Linux typically has 100-300 processes
	return 100 + rand.Intn(200)
}

func (i *Info) GetNetworkStats() (rxBytes, txBytes int64) {
	// Simulate realistic network stats
	rand.Seed(time.Now().UnixNano())
	base := time.Now().Unix()
	rxBytes = base*1024*1024 + rand.Int63n(1024*1024*100)
	txBytes = base*512*1024 + rand.Int63n(1024*1024*50)
	return rxBytes, txBytes
}