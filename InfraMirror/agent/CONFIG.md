# InfraMirror Agent Configuration

## Configuration Structure

```
agent/
├── global.yml              # Main agent configuration (required)
├── config/                 # Your active configurations (gitignored)
│   └── (user creates structure here)
└── example/               # Example configurations (templates)
    ├── instance/config.yml
    ├── docker/config.yml
    ├── mongo/config.yml
    ├── postgres/config.yml
    └── redis/config.yml
```

## Quick Start

### 1. Configure Agent (Required)

Edit `global.yml`:
```yaml
api_key: "your-api-key-from-ui"
name: "my-agent"
region: "US East"
datacenter: "Virginia DC1"

backend:
  url: "http://localhost:8080"
```

### 2. Enable Monitoring Modules

Copy example configs to `config/` directory:

```bash
# Enable instance monitoring
mkdir -p config/instance
cp example/instance/config.yml config/instance/config.yml

# Enable MongoDB monitoring
mkdir -p config/mongo
cp example/mongo/config.yml config/mongo/config.yml
vi config/mongo/config.yml  # Edit connection details

# Enable PostgreSQL monitoring
mkdir -p config/postgres
cp example/postgres/config.yml config/postgres/config.yml
vi config/postgres/config.yml  # Edit connection details
```

### 3. Start Agent

```bash
./agent
```

## Configuration Files

### global.yml (Required)
Main agent configuration with API key, region, datacenter, and backend URL.

### config/**/*.yml (Optional)
Enable specific monitoring modules by copying from `example/` and editing.

**Available modules:**
- `instance/config.yml` - Monitor this server (ping + hardware metrics)
- `docker/config.yml` - Monitor Docker containers
- `mongo/config.yml` - Monitor MongoDB
- `postgres/config.yml` - Monitor PostgreSQL
- `redis/config.yml` - Monitor Redis

## Command Line Options

```bash
./agent [options]

Options:
  -global string
        Path to global configuration file (default "global.yml")
  -config-dir string
        Directory containing configuration files (default "./config")
  -data string
        Data directory for persistent storage (default "./data")
```

## Examples

### Run with custom config directory
```bash
./agent -config-dir /etc/inframirror/config
```

### Run with custom global config
```bash
./agent -global /etc/inframirror/global.yml
```

## Notes

- `example/` directory contains templates - DO NOT edit these
- `config/` directory is gitignored - create your own structure
- Agent loads ALL `.yml` files from `config/` directory recursively
- Each module can be enabled/disabled independently
- You can organize configs however you want (flat or nested)
