# Instance FTS Migration - Remaining Steps

## ✅ Completed
1. Liquibase changelog - Added search_vector, GIN index, trigger, trigram indexes
2. Instance.java - Removed @Document and @Field annotations
3. InstanceRepository.java - Added 4 FTS search methods
4. InstanceSearchResultDTO.java - Created
5. InstanceService.java - Added 4 method signatures, removed reindexAll

## 🔄 Remaining Steps

### 6. InstanceServiceImpl.java
- Remove: InstanceSearchRepository dependency
- Remove: reindexAll() method
- Remove: All .index() and .delete() calls to Elasticsearch
- Add: Implement search(), searchPrefix(), searchFuzzy(), searchWithHighlight() using FullTextSearchUtil

### 7. InstanceQueryService.java
- Remove: InstanceSearchRepository import and dependency

### 8. InstanceResource.java
- Remove: ElasticsearchExceptionMapper import
- Remove: try-catch with ElasticsearchExceptionMapper in search endpoint
- Add: 3 new endpoints: /_search/prefix, /_search/fuzzy, /_search/highlight

### 9. InstanceResourceIT.java
- Remove: All Elasticsearch imports (await, IterableUtil, Streamable)
- Remove: InstanceSearchRepository dependency
- Remove: All searchDatabaseSize variables and await() calls
- Add: 6 FTS tests with em.flush()/em.clear() pattern

### 10. Delete
- src/main/java/vibhuvi/oio/inframirror/repository/search/InstanceSearchRepository.java

---

## Quick Commands

```bash
# Delete InstanceSearchRepository
rm src/main/java/vibhuvi/oio/inframirror/repository/search/InstanceSearchRepository.java

# Compile to check for errors
./mvnw clean compile -q
```

## Test After Completion
```bash
./mvnw test -Dtest=InstanceResourceIT
```
