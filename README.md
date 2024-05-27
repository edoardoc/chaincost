# Chaincost
Credit Card Cost API exercise 

## Story
- the full card number is invoked to the endpoint /payment-cards-cost 
- the system in turns invokes https://bintable.com/get-api with only a part of the number (IIN) and gets the card issuing country (using an internal cache to avoid useless calls) 
- the card issuing country code is matched with a clearing cost matrix to know and return the clearing cost 

Solution based on a MSSAPI "Microservices API" architecture, using Open Liberty java services, with a service for the clearing cost matrix (Clearingcost), a service for the card number lookup (Summer) and a service for the caching of credit card IIN codes (Iincache)

## Service Summer (API Gateway) 
Summer is the service that receives the full card number and returns the clearing cost. It is the entry point for the API. It is also responsible for the IIN lookup and the clearing cost lookup, and the invoking of Fetcher to fetch the IIN data from the external provider.



### Endpoint /payment-cards-cost 
```
curl -Ss -X POST http://localhost:9081/summer/api/v1/payment-cards-cost -d "card_number=517862543698"
```

## Service Clearingcost (Persisted Clearing Cost Matrix table)
Clearingcost is the service that manages the clearing cost matrix. It is a simple key-value store for the clearing cost of a country, it stores the clearing cost matrix in a serialized map on the filesystem.

### Endpoint(s) managing the clearing cost matrix table
The data storage for the clearing cost matrix is a serialized map, internal to the microservice clearingcost
- List (returns all the clearing cost matrix)
- Create (new country <--> clearing cost item)
- Update 
- Delete
- Read (returns cost for a country)

### GET method on main endpoint (List)
```
curl -v http://localhost:9080/clearingcost/|jq
```

### PUT method  (Create and Update)
```
curl -vX PUT http://localhost:9080/clearingcost -H "content-type:application/json" --data-raw '{"country": "fr", "cost": "113"}'
```

### DELETE method
```
curl -vX DELETE http://localhost:9080/clearingcost/GB
```

### GET method on main country endpoint (Read)
```
curl -v http://localhost:9080/clearingcost/gr|jq
```


## Service IINCache (Persisted cache of IIN's from external provider)
Iincache is the service that manages the cache of IIN's. It is a simple key-value store for the IIN's and their alpha2 country code. Its purpose is to avoid unnecessary calls to the external provider, it stores the IIN's and their alpha2 country code in a serialized map.

### Endpoint(s) managing iincache
- Create (new iin <--> alpha2 mapping)
- Delete
- Read (returns alpha2 for iin)

### PUT method stores IIN data in cache
```
curl -vX PUT http://localhost:9082/iincache/51786254 -H "content-type:application/json" --data-raw 'ag'
```

### GET method on main IIN endpoint
```
curl -v http://localhost:9082/iincache/51786254|jq
```

### DELETE method
```
curl -vX DELETE http://localhost:9082/iincache/517862543698
```


# Build and Run
To run the services in your local java environment for development / debugging, in the main dir:

```
mvn -pl summer liberty:run  # it will run on port 9081
mvn -pl clearingcost liberty:run # port 9080
mvn -pl iincache liberty:run # port 9082
```

To run the services in docker, in the main dir:

```
mvn package # to build the war
docker-compose up # to start
```

to run a service so that you can change the code
```
mvn -pl summer liberty:dev
mvn -pl clearingcost liberty:dev
mvn -pl iincache liberty:dev
```

to run a service in docker so that you can change the code
```
mvn -pl summer liberty:devc # runs development in docker
mvn -pl iincache liberty:devc # runs development in docker
mvn -pl clearingcost liberty:devc # runs development in docker
```
