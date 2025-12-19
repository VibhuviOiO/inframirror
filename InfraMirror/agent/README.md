# InfraMirror Agent

A lightweight monitoring agent for InfraMirror that auto-discovers infrastructure, registers itself, and continuously reports metrics. Built with Go for high performance and minimal resource usage.

## Features

- **Self-Registration**: Automatically registers with backend using API key
- **Auto-Discovery**: Scans local network for instances and services
- **Multi-Type Monitoring**: HTTP, Instance (ping + hardware), and Service (TCP) monitoring
- **High Availability**: Leader election for multi-agent deployments
- **Hot Reload**: Development mode with automatic restart on code changes
- **Persistent Caching**: Stores agent metadata to avoid duplicate API calls

## Quick Start

### Development Mode (Docker)

```bash
# Start with hot reload
make dev

# Or manually
docker-compose -f docker-compose.dev.yml --profile dev up --build
```

### Native Binary

```bash
# Build and run
make build
make run

# Or directly
go build -o build/inframirror-agent .
./build/inframirror-agent --global global.yml --instance example/instance/config.yml
```

## Configuration

### Global Configuration (`global.yml`)

```yaml
api_key: "your-api-key-here"
name: ""  # defaults to hostname
region: "US East"
datacenter: "Virginia DC1"

backend:
  url: "http://localhost:8080"
  timeout: "10s"

agent:
  heartbeat_interval: 30  # seconds
```

### Instance Configuration (`example/instance/config.yml`)

```yaml
instance:
  enable: true
  
  # Ping monitoring
  ping_enabled: true
  ping_interval: 30        # seconds
  ping_timeout_ms: 3000    # milliseconds
  ping_retry_count: 2      # retries
  
  # Hardware monitoring
  hardware_monitoring_enabled: true
  hardware_monitoring_interval: 300  # seconds
  
  # Thresholds
  cpu_warning_threshold: 70     # percent
  cpu_danger_threshold: 90      # percent
  memory_warning_threshold: 75  # percent
  memory_danger_threshold: 90   # percent
  disk_warning_threshold: 80    # percent
  disk_danger_threshold: 95     # percent
```

## Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Go Agent      │───▶│  Java Backend    │───▶│   PostgreSQL    │
│                 │    │                  │    │                 │
│ • Registration  │    │ • Agent API      │    │ • Agents        │
│ • Discovery     │    │ • Instance API   │    │ • Instances     │
│ • Monitoring    │    │ • Heartbeat API  │    │ • Heartbeats    │
│ • Heartbeats    │    │ • HTTP Monitors  │    │ • Monitors      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

## Development

### Prerequisites

- Go 1.21+
- Docker & Docker Compose
- Make (optional)

### Available Commands

```bash
make help          # Show all available commands
make dev           # Start development mode with hot reload
make build         # Build binary
make test          # Run tests
make clean         # Clean build artifacts
make docker        # Build Docker image
make fmt           # Format code
make lint          # Lint code (requires golangci-lint)
```

### Project Structure

```
agent/
├── cmd/                    # Command-line interface (future)
├── config/                 # Configuration management
├── internal/               # Private application code
│   ├── api/               # Backend API client
│   ├── cache/             # Persistent caching
│   ├── discovery/         # Network discovery
│   ├── heartbeat/         # Heartbeat managers
│   ├── lock/              # HA coordination
│   ├── monitor/           # Monitoring logic
│   ├── services/          # Service detection
│   └── system/            # System information
├── example/               # Example configurations
├── data/                  # Runtime data (cached)
├── global.yml             # Global configuration
├── docker-compose.dev.yml # Development setup
├── Dockerfile.dev         # Development container
└── main.go               # Application entry point
```

## Monitoring Types

### Instance Monitoring
- **Ping Checks**: ICMP/TCP connectivity and latency
- **Hardware Metrics**: CPU, memory, disk, network usage
- **System Info**: OS type, platform, uptime

### HTTP Monitoring
- **Endpoint Checks**: HTTP/HTTPS availability and performance
- **Response Validation**: Status codes, response times
- **Regional Monitoring**: Multi-agent support for geographic distribution

### Service Monitoring (Future)
- **TCP Port Checks**: Database, message queue connectivity
- **Service-Specific Health**: PostgreSQL, Redis, MongoDB, etc.
- **Cluster Monitoring**: Multi-node service health

## API Integration

The agent communicates with the InfraMirror backend via REST APIs:

- `POST /api/agent/register` - Self-registration
- `POST /api/agent/heartbeat` - Agent liveness
- `POST /api/agent/instances` - Instance creation
- `POST /api/heartbeats/batch` - Bulk heartbeat submission

## High Availability

Multiple agents can be deployed for the same monitoring scope. The agent uses a leader election mechanism to ensure only one agent performs monitoring at a time, with automatic failover.

## Performance

- **CPU Usage**: < 5% average
- **Memory Usage**: < 100 MB
- **Network**: < 1 MB/min
- **Disk**: < 10 MB logs per day

## Troubleshooting

### Common Issues

1. **Connection Refused**: Check backend URL in `global.yml`
2. **Invalid API Key**: Verify API key in backend admin panel
3. **Leadership Timeout**: Check if multiple agents are running
4. **Instance Creation Failed**: Verify datacenter exists and agent has permissions

### Logs

```bash
# Docker logs
docker logs inframirror-agent-dev

# Native binary logs
./build/inframirror-agent --global global.yml --instance example/instance/config.yml
```

## License

This project is part of the InfraMirror monitoring platform.