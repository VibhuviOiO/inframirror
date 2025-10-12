## Docker APIs

```bash
# List Containers
curl http://192.168.0.103:2375/containers/json?all=1

# Get logs of a container  - Is is actually a streaming API will not work for the curl either print on the terminal or to a filr following curl will not work. but i want to use this API at Application backend preperation. 

curl http://192.168.0.103:2375/containers/<CONTAINER_ID>/logs?stdout=1&stderr=1
    ## Follow logs (like docker logs -f)
    curl -s "http://192.168.0.103:2375/containers/b6f00abd266096838a148c1512ea8cdabbb47beeb4932e9bce9a29951427c260/logs?stdout=1&stderr=1&follow=1"  --output - 
    Above API is fro testing curl, but in the 


    ## JUMP Logs on terminal - Works
    curl -s http://192.168.0.103:2375/containers/b6f00abd266096838a148c1512ea8cdabbb47beeb4932e9bce9a29951427c260/logs?stdout=1&stderr=1 --output -
    ## save logs to a files
    curl -s http://192.168.0.103:2375/containers/<container_id>/logs?stdout=1&stderr=1 -o container.log
    cat container.log
    ## Tails Logs 
    curl -s "http://192.168.0.103:2375/containers/b6f00abd266096838a148c1512ea8cdabbb47beeb4932e9bce9a29951427c260/logs?stdout=1&stderr=1&tail=10" --output -
    ##


# List Images:
curl http://192.168.0.103:2375/images/json

# Inspect 
curl http://192.168.0.103:2375/containers/<container_id>/json

# Stats
curl http://192.168.0.103:2375/containers/<container_id>/stats?stream=0
```
 curl -G "http://localhost:8080/api/dockerops/containers" \
  --data-urlencode "host=192.168.1.10" \
  --data-urlencode "port=2375" \
  --data-urlencode "protocol=http" \
  --data-urlencode "all=true"
