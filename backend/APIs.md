# --- ENVIRONMENT API ---

# List all environments
curl http://localhost:8080/environments

# Get an environment by ID (replace 1 with a valid ID)
curl http://localhost:8080/environments/1

# Create a new environment
curl -X POST http://localhost:8080/environments \
  -H "Content-Type: application/json" \
  -d '{"name":"dev"}'

# Update an environment (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/environments/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"stage"}'

# Delete an environment (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/environments/1
# --- APPLICATION API ---

# List all applications
curl http://localhost:8080/applications

# Get an application by ID (replace 1 with a valid ID)
curl http://localhost:8080/applications/1


# Create a new application (replace IDs with valid ones)
curl -X POST http://localhost:8080/applications \
  -H "Content-Type: application/json" \
  -d '{"datacenterId":1,"catalogId":1,"environmentId":1}'

# Update an application (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/applications/1 \
  -H "Content-Type: application/json" \
  -d '{"datacenterId":1,"catalogId":1,"environmentId":2}'

# Delete an application (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/applications/1
# --- SERVICE API ---

# List all services
curl http://localhost:8080/services

# Get a service by ID (replace 1 with a valid ID)
curl http://localhost:8080/services/1

# Create a new service
curl -X POST http://localhost:8080/services \
  -H "Content-Type: application/json" \
  -d '{"datacenterId":1,"hostId":1,"catalogId":1}'

# Update a service (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/services/1 \
  -H "Content-Type: application/json" \
  -d '{"datacenterId":1,"hostId":1,"catalogId":1}'

# Delete a service (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/services/1
# --- HOST API ---

# List all hosts
curl http://localhost:8080/hosts

# Get a host by ID (replace 1 with a valid ID)
curl http://localhost:8080/hosts/1

# Create a new host
curl -X POST http://localhost:8080/hosts \
  -H "Content-Type: application/json" \
  -d '{"datacenterId":1,"hostname":"host1","privateIP":"10.0.0.10","kind":"VM"}'

# Update a host (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/hosts/1 \
  -H "Content-Type: application/json" \
  -d '{"hostname":"host1-updated","privateIP":"10.0.0.11","kind":"Physical"}'

# Delete a host (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/hosts/1
# --- APPLICATION CATALOG API ---

# List all application catalogs
curl http://localhost:8080/application-catalogs

# Get an application catalog by ID (replace 1 with a valid ID)
curl http://localhost:8080/application-catalogs/1


# Create a new application catalog
curl -X POST http://localhost:8080/application-catalogs \
  -H "Content-Type: application/json" \
  -d '{"name":"New App","uniqueId":"app-003","appTypeId":1,"teamId":1}'

# Update an application catalog (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/application-catalogs/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated App","uniqueId":"app-003","appTypeId":1,"teamId":1}'

# Delete an application catalog (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/application-catalogs/1
# --- SERVICE CATALOG API ---

# List all service catalogs
curl http://localhost:8080/service-catalogs

# Get a service catalog by ID (replace 1 with a valid ID)
curl http://localhost:8080/service-catalogs/1

# Create a new service catalog
curl -X POST http://localhost:8080/service-catalogs \
  -H "Content-Type: application/json" \
  -d '{"name":"New Catalog","serviceTypeId":1}'

# Update a service catalog (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/service-catalogs/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Catalog","serviceTypeId":1}'

# Delete a service catalog (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/service-catalogs/1

# --- SERVICE OR APP TYPE API ---

# List all service or app types
curl http://localhost:8080/service-or-app-types

# Get a service or app type by ID (replace 1 with a valid ID)
curl http://localhost:8080/service-or-app-types/1

# Create a new service or app type
curl -X POST http://localhost:8080/service-or-app-types \
  -H "Content-Type: application/json" \
  -d '{"name":"New Type"}'

# Update a service or app type (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/service-or-app-types/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Type"}'

# Delete a service or app type (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/service-or-app-types/1
# --- REGION API ---

# List all regions
curl http://localhost:8080/regions

# Get a region by ID (replace 1 with a valid ID)
curl http://localhost:8080/regions/1

# Create a new region
curl -X POST http://localhost:8080/regions \
  -H "Content-Type: application/json" \
  -d '{"name":"New Region"}'

# Update a region (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/regions/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Name"}'

# Delete a region (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/regions/1

# --- DATACENTER API ---

# List all datacenters
curl http://localhost:8080/datacenters

# Get a datacenter by ID (replace 1 with a valid ID)
curl http://localhost:8080/datacenters/1

# Create a new datacenter (replace regionId with a valid region ID)
curl -X POST http://localhost:8080/datacenters \
  -H "Content-Type: application/json" \
  -d '{"name":"New DC","shortName":"ndc","regionId":1}'

# Update a datacenter (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/datacenters/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated DC","shortName":"udc","regionId":1}'

# Delete a datacenter (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/datacenters/3

# --- TEAM API ---

# List all teams
curl http://localhost:8080/teams

# Get a team by ID (replace 1 with a valid ID)
curl http://localhost:8080/teams/1

# Create a new team
curl -X POST http://localhost:8080/teams \
  -H "Content-Type: application/json" \
  -d '{"name":"New Team"}'

# Update a team (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/teams/4 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Team"}'

# Delete a team (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/teams/4