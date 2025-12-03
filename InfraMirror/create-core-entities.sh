#!/bin/bash

# InfraMirror Core Entity Creation Script
# Creates only the essential entities you need

set -e

echo "🚀 Creating Core InfraMirror Entities"
echo "Your stack: PostgreSQL + Elasticsearch + Keycloak + Redis"
echo ""

# Check if we're in the right directory
if [ ! -f "pom.xml" ] || [ ! -f ".yo-rc.json" ]; then
    echo "❌ Error: Please run this script from the InfraMirror root directory"
    exit 1
fi

echo "📋 Creating entities in dependency order..."
echo ""

# Core infrastructure entities
echo "1️⃣  Creating Region entity..."
jhipster entity Region --skip-tests --force

echo "2️⃣  Creating Datacenter entity..."
jhipster entity Datacenter --skip-tests --force

echo "3️⃣  Creating Instance entity (your machines)..."
jhipster entity Instance --skip-tests --force

echo "4️⃣  Creating Agent entity (deployed on machines)..."
jhipster entity Agent --skip-tests --force

echo "5️⃣  Creating Application entity (Node.js/Java apps)..."
jhipster entity Application --skip-tests --force

echo "6️⃣  Creating Service entity (Kafka/ES/Mongo clusters)..."
jhipster entity Service --skip-tests --force

echo "7️⃣  Creating RemoteSession entity (SSH-like sessions)..."
jhipster entity RemoteSession --skip-tests --force

echo "8️⃣  Creating LogEntry entity (for Elasticsearch)..."
jhipster entity LogEntry --skip-tests --force

echo ""
echo "✅ All core entities created!"
echo ""
echo "🔧 Next steps:"
echo "1. Run: ./mvnw liquibase:update"
echo "2. Run: ./mvnw spring-boot:run"
echo "3. Open: http://localhost:8080"
echo "4. Test entities through Admin > Entities menu"
echo ""
echo "📚 Check ESSENTIAL_ENTITIES.md for configuration details"