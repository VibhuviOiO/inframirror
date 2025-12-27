# FTS Migration Plan for Instance, MonitoredService, HttpMonitor

## Instance Entity
**Searchable Fields**: name, hostname, description, privateIpAddress, publicIpAddress

**Files to modify**:
1. Liquibase: Add search_vector, GIN index, trigger, trigram indexes
2. Instance.java: Remove @Document and @Field annotations
3. InstanceRepository.java: Add 4 search methods
4. InstanceSearchResultDTO.java: Create (extends InstanceDTO)
5. InstanceService.java: Add 4 method signatures
6. InstanceServiceImpl.java: Implement using FullTextSearchUtil, remove Elasticsearch
7. InstanceQueryService.java: Remove InstanceSearchRepository
8. InstanceResource.java: Add 4 endpoints, remove ElasticsearchExceptionMapper
9. InstanceResourceIT.java: Remove Elasticsearch tests, add 6 FTS tests
10. Delete: InstanceSearchRepository.java

## MonitoredService Entity
**Searchable Fields**: name, serviceType, description

**Files to modify**: Same pattern as Instance

## HttpMonitor Entity  
**Searchable Fields**: name, url, description

**Files to modify**: Same pattern as Instance

---

## Execution Order
1. Instance (most complex - 5 searchable fields)
2. MonitoredService (3 searchable fields)
3. HttpMonitor (3 searchable fields)

Each entity takes ~15-20 minutes following the Datacenter pattern.
