# Infra MIЯROR

- Datacenters belongs to Three Regiosn(APAC, EU, US)
- Create the datacenter: 

```bash
curl -s -X POST http://localhost:8080/datacenters \
  -H "Content-Type: application/json" \
  -d '{"name":"Example DC","shortName":"exdc"}' | jq
# expected: JSON of created datacenter with id
```

Settings: 
- service catalog: mesos, marathon, consul, cassandra, haproxy
Infra
- datacenter: lon
- machiens : lon-inf-[1-4], lon-lb[1-2],lon-cass-[101-106], lon-rt-[101-104]
Services: 
- marathon, on lon-inf-[1-4] with port 8080, 
- haproxy, on lon-lb[1-2] with port 80, and 443 adn stats on 9000 and so on. 
- so on
Applications
- Marathon Apps: 
  datacenters : [list of datacenter], Appname, appid, current version, SearchServiceJ11 - marathon Type, deployed into 10 datanters as tags, each marathon application type need to make theeir api call to get the tasks definition and status of the application configuration. 
- rrserver : design the UI for readinesprobe, and health on all the lon-rt-[101-104], get the current version health 

