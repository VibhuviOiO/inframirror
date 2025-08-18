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
curl -X PUT http://localhost:8080/teams/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Team"}'

# Delete a team (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/teams/1