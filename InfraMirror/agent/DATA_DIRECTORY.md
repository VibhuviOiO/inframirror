# Agent Data Directory Structure

The InfraMirror agent uses a persistent data directory to store cache and runtime data.

## Default Structure
```
./data/
├── agent-cache.json    # Cached agent, region, datacenter IDs
├── locks/              # Agent lock files (future)
└── logs/               # Agent logs (future)
```

## Cache File Format
```json
{
  "agentId": 123,
  "regionId": 45,
  "datacenterId": 67,
  "region": "US East",
  "datacenter": "Virginia DC1"
}
```

## Usage
```bash
# Default data directory
./agent

# Custom data directory
./agent -data /var/lib/inframirror-agent

# Docker volume mount
docker run -v /host/data:/data inframirror/agent -data /data
```

## Benefits
- **Persistent Cache**: Survives agent restarts
- **Fast Startup**: Skip registration on restart
- **Efficient**: No duplicate API calls
- **Configurable**: Custom data directory location