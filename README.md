# Infra MIЯROR


- Create the datacenter: 

```bash
curl -s -X POST http://localhost:8080/datacenters \
  -H "Content-Type: application/json" \
  -d '{"name":"Example DC","shortName":"exdc"}' | jq
# expected: JSON of created datacenter with id
```