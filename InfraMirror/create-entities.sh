#!/bin/bash

# InfraMirror Entity Creation Script
# This script creates all the required entities for the InfraMirror platform

set -e

echo "🚀 Starting InfraMirror Entity Creation..."
echo "Make sure you're in the InfraMirror directory and JHipster is installed"

# Check if we're in the right directory
if [ ! -f "pom.xml" ] || [ ! -f ".yo-rc.json" ]; then
    echo "❌ Error: Please run this script from the InfraMirror root directory"
    exit 1
fi

# Function to create entity with confirmation
create_entity() {
    local entity_name=$1
    echo ""
    echo "📝 Creating entity: $entity_name"
    read -p "Do you want to create $entity_name entity? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "Creating $entity_name..."
        jhipster entity $entity_name --skip-tests --force
        echo "✅ $entity_name created successfully"
    else
        echo "⏭️  Skipping $entity_name"
    fi
}

# Core Infrastructure Entities (in dependency order)
echo ""
echo "🏗️  Creating Core Infrastructure Entities..."

create_entity "Region"
create_entity "Datacenter" 
create_entity "Agent"
create_entity "Instance"

# Application & Service Management
echo ""
echo "📱 Creating Application & Service Management Entities..."

create_entity "Application"
create_entity "Service"

# Monitoring & Metrics
echo ""
echo "📊 Creating Monitoring & Metrics Entities..."

create_entity "MetricsMetadata"
create_entity "SystemMetrics"
create_entity "LogEntry"

# Security & Sessions
echo ""
echo "🔐 Creating Security & Session Entities..."

create_entity "RemoteSession"

# Plugin System
echo ""
echo "🔌 Creating Plugin System Entities..."

create_entity "Plugin"
create_entity "PluginInstance"

echo ""
echo "🎉 Entity creation process completed!"
echo ""
echo "📋 Next Steps:"
echo "1. Review the generated entities in src/main/java/vibhuvi/oio/inframirror/domain/"
echo "2. Update the Liquibase changelogs if needed"
echo "3. Run './mvnw liquibase:update' to apply database changes"
echo "4. Start the application with './mvnw spring-boot:run'"
echo "5. Test the entities through the API or admin interface"
echo ""
echo "📚 For detailed configuration, check ENTITY_CREATION_GUIDE.md"