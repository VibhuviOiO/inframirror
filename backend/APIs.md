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
curl -X PUT http://localhost:8080/api/regions/6 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Name"}'

# Delete a region (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/regions/6
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
  -d '{"name":"Virginia","shortName":"VA","regionId":4,"privateCIDR":"10.0.0.0/16","publicCIDR":"52.0.0.0/16"}'

# Update a datacenter (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/datacenters/6 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated DC","shortName":"udc","regionId":1,"privateCIDR":"10.0.1.0/16","publicCIDR":"52.0.1.0/16"}'

# Delete a datacenter (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/datacenters/6
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
    "environmentId": 1,
    "datacenterId": 1
  }'

### Update a cluster (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/clusters/6 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "cluster1-updated",
    "environmentId": 2,
    "datacenterId": 1
  }'

### Delete a cluster (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/clusters/6
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
  -d '{"name":"TEST_2"}'

# Update an environment (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/environments/11 \
  -H "Content-Type: application/json" \
  -d '{"name":"TEST_11"}'

# Delete an environment (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/environments/11
```




# INTEGRATION API
```bash
# List all integrations
curl http://localhost:8080/api/integrations

# Get an integration by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/integrations/1

# Create a new integration
curl -X POST http://localhost:8080/api/integrations \
  -H "Content-Type: application/json" \
  -d '{"name":"TEST Postgres","integrationType":"Database","version":"15.0","description":"Postgres DB","enabled":true}'

# Update an integration (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/integrations/11 \
  -H "Content-Type: application/json" \
  -d '{"name":"TEST Postgres-updated","integrationType":"Database","version":"15.1","description":"Updated desc","enabled":false}'

# Delete an integration (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/integrations/11
```


# INTEGRATION INSTANCE API
```bash
# List all integration instances
curl http://localhost:8080/api/integration-instances

# Get an integration instance by ID (replace 1 with a valid ID)
curl http://localhost:8080/api/integration-instances/1

# Create a new integration instance
curl -X POST http://localhost:8080/api/integration-instances \
  -H "Content-Type: application/json" \
  -d '{"datacenterId": 22,"hostId": 25,"environmentId": 4,"integrationId": 22,"port": 5432,"config": { "integrations": ["Docker"] }}'

# Update an integration instance (replace 1 with a valid ID)
curl -X PUT http://localhost:8080/api/integration-instances/21 \
  -H "Content-Type: application/json" \
  -d '{"hostId":1,"integrationId":2,"enabled":false,"port":5433,"config":{"user":"admin2"}}'

# Delete an integration instance (replace 1 with a valid ID)
curl -X DELETE http://localhost:8080/api/integration-instances/21
```
# POSTGRES API 
```bash
# list tables in given paramaters. 

 curl -X POST http://localhost:8080/api/postgresops/tables \
  -H "Content-Type: application/json" \
  -d '{
    "host": "127.0.0.1",
    "port": 5432,
    "database": "inframirror",
    "user": "inframirror",
    "password": "inframirror"
  }'
{"tables":["_prisma_migrations","Environment","Region","Datacenter","Host","Cluster","IntegrationInstance","Integration"]}%                                
```