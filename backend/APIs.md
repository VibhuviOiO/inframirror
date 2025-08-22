# TEAM API
```bash
# List
curl http://localhost:8080/api/teams

# Get a team by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/teams/1

# Create a new team
curl -X POST http://localhost:8080/api/teams \
  -H "Content-Type: application/json" \
  -d '{"name":"Devops"}'

# Update a team (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/teams/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Team"}'

# Delete a team (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/teams/1
```

# REGION API
```bash
# List all regions
curl http://localhost:8080/api/regions

# Get a region by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/regions/1

# Create a new region
curl -X POST http://localhost:8080/api/regions \
  -H "Content-Type: application/json" \
  -d '{"name":"New Region"}'

# Update a region (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/regions/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Name"}'

# Delete a region (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/regions/1
```

# DATACENTER API
```bash
# List all datacenters
curl http://localhost:8080/api/datacenters

# Get a datacenter by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/datacenters/1

# Create a new datacenter (replace regionId with a valid region ID)
curl -X POST http://localhost:8080/api/datacenters \
  -H "Content-Type: application/json" \
  -d '{"name":"New DC","shortName":"ndc","regionId":1}'

# Update a datacenter (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/datacenters/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated DC","shortName":"udc","regionId":1}'

# Delete a datacenter (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/datacenters/3
```


# CLUSTER API

```bash
### List all clusters
curl http://localhost:8080/api/clusters

### Get a cluster by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/clusters/1

### Create a new cluster

curl -X POST http://localhost:8080/api/clusters \
  -H "Content-Type: application/json" \
  -d '{
    "name": "cluster1",
    "catalogId": 1,
    "environmentId": 1,
    "datacenterId": 1
  }'

### Update a cluster (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/clusters/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "cluster1-updated",
    "catalogId": 1,
    "environmentId": 2,
    "datacenterId": 1
  }'

### Delete a cluster (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/clusters/1
```

# HOST API
```bash
### List all hosts
curl http://localhost:8080/api/hosts

### Get a host by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/hosts/1

### Create a new host
curl -X POST http://localhost:8080/api/hosts \
  -H "Content-Type: application/json" \
  -d '{
    "datacenterId": 1,
    "hostname": "host1",
    "privateIP": "10.0.0.10",
    "publicIP": "10.0.0.20",
    "kind": "VM",
    "tags": {"os": "ubuntu", "env": "prod"}
  }'

### Update a host (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/hosts/1 \
  -H "Content-Type: application/json" \
  -d '{
    "hostname": "host1-updated",
    "privateIP": "10.0.0.11",
    "publicIP": "10.0.0.21",
    "kind": "Physical",
    "tags": {"os": "centos"}
  }'

### Delete a host (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/hosts/1

## Notes
- `datacenterId` must reference an existing datacenter.
- `kind` must be one of: `VM`, `Physical`, `BareMetal`.
- `tags` is a JSON object for arbitrary metadata (optional).
```

# ENVIRONMENT API
```bash
# List all environments
curl http://localhost:8080/api/environments

# Get an environment by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/environments/1

# Create a new environment
curl -X POST http://localhost:8080/api/environments \
  -H "Content-Type: application/json" \
  -d '{"name":"dev"}'

# Update an environment (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/environments/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"stage"}'

# Delete an environment (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/environments/1
```


# SERVICE API

```bash
# List all services
curl http://localhost:8080/api/services

# Get a service by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/services/1

# Create a new service
curl -X POST http://localhost:8080/api/services \
  -H "Content-Type: application/json" \
  -d '{"datacenterId":1,"hostId":1,"catalogId":1}'

# Update a service (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/services/1 \
  -H "Content-Type: application/json" \
  -d '{"datacenterId":1,"hostId":1,"catalogId":1}'

# Delete a service (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/services/1
```

# CATALOG TYPE API

```bash
# List all catalog types
curl http://localhost:8080/api/catalog-types

# Get a catalog type by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/catalog-types/1

# Create a new catalog type
curl -X POST http://localhost:8080/api/catalog-types \
  -H "Content-Type: application/json" \
  -d '{"name":"New Type", "description":"Type description"}'

# Update a catalog type (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/catalog-types/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Type", "description":"Updated description"}'

# Delete a catalog type (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/catalog-types/1
```

# CATALOG API
```bash
# List all catalogs
curl http://localhost:8080/api/catalogs

# Get a catalog by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/catalogs/1

# Create a new catalog
curl -X POST http://localhost:8080/api/catalogs \
  -H "Content-Type: application/json" \
  -d '{"name":"New Catalog","catalogTypeId":1,"uniqueId":"cat-001","defaultPort":1234,"description":"desc","gitRepoUrl":"https://repo","teamId":1}'

# Update a catalog (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/catalogs/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Catalog","catalogTypeId":1,"uniqueId":"cat-001","defaultPort":1234,"description":"desc","gitRepoUrl":"https://repo","teamId":1}'

# Delete a catalog (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/catalogs/1
```