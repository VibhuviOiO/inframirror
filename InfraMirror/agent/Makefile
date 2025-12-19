# InfraMirror Agent Makefile

# Variables
BINARY_NAME=inframirror-agent
VERSION?=1.0.0
BUILD_DIR=build
DOCKER_IMAGE=inframirror/agent
DOCKER_TAG?=latest

# Go parameters
GOCMD=go
GOBUILD=$(GOCMD) build
GOCLEAN=$(GOCMD) clean
GOTEST=$(GOCMD) test
GOGET=$(GOCMD) get
GOMOD=$(GOCMD) mod

# Build flags
LDFLAGS=-ldflags "-X main.Version=$(VERSION)"

.PHONY: all build clean test deps docker docker-dev run dev help

# Default target
all: clean deps test build

# Build the binary
build:
	@echo "Building $(BINARY_NAME)..."
	@mkdir -p $(BUILD_DIR)
	$(GOBUILD) $(LDFLAGS) -o $(BUILD_DIR)/$(BINARY_NAME) .

# Clean build artifacts
clean:
	@echo "Cleaning..."
	$(GOCLEAN)
	@rm -rf $(BUILD_DIR)
	@rm -rf tmp/

# Run tests
test:
	@echo "Running tests..."
	$(GOTEST) -v ./...

# Download dependencies
deps:
	@echo "Downloading dependencies..."
	$(GOMOD) download
	$(GOMOD) tidy

# Build Docker image
docker:
	@echo "Building Docker image..."
	docker build -t $(DOCKER_IMAGE):$(DOCKER_TAG) .

# Build development Docker image
docker-dev:
	@echo "Building development Docker image..."
	docker build -f Dockerfile.dev -t $(DOCKER_IMAGE):dev .

# Run locally
run: build
	@echo "Running $(BINARY_NAME)..."
	./$(BUILD_DIR)/$(BINARY_NAME) --global global.yml --instance example/instance/config.yml

# Development mode with hot reload
dev:
	@echo "Starting development mode..."
	docker-compose -f docker-compose.dev.yml --profile dev up --build

# Stop development containers
dev-stop:
	@echo "Stopping development containers..."
	docker-compose -f docker-compose.dev.yml --profile dev down

# Format code
fmt:
	@echo "Formatting code..."
	$(GOCMD) fmt ./...

# Lint code (requires golangci-lint)
lint:
	@echo "Linting code..."
	golangci-lint run

# Install development tools
install-tools:
	@echo "Installing development tools..."
	$(GOGET) github.com/air-verse/air@latest
	$(GOGET) github.com/golangci/golangci-lint/cmd/golangci-lint@latest

# Show help
help:
	@echo "Available targets:"
	@echo "  all          - Clean, download deps, test, and build"
	@echo "  build        - Build the binary"
	@echo "  clean        - Clean build artifacts"
	@echo "  test         - Run tests"
	@echo "  deps         - Download dependencies"
	@echo "  docker       - Build Docker image"
	@echo "  docker-dev   - Build development Docker image"
	@echo "  run          - Run locally"
	@echo "  dev          - Start development mode with hot reload"
	@echo "  dev-stop     - Stop development containers"
	@echo "  fmt          - Format code"
	@echo "  lint         - Lint code"
	@echo "  install-tools- Install development tools"
	@echo "  help         - Show this help"