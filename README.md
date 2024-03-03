# Chaincost
Credit Card Cost API exercise 

## Story
- the full card number is invoked to the endpoint /payment-cards-cost 
- the system in turns invokes https://bintable.com/get-api with only a part of the number (IIN) and gets the card issuing country (using an internal cache to avoid useless calls) 
- the card issuing country code is matched with a clearing cost matrix to know and return the clearing cost 

Solution based on a MSSAPI "Microservices API" architecture, using Open Liberty java services, with a service for the clearing cost matrix (Clearingcost), a service for the card number lookup (Summer) and a service for the caching of credit card IIN codes (Iincache)

## Service Summer (API Gateway) 
### Endpoint /payment-cards-cost 

```
curl -Ss -X POST http://localhost:9081/summer/api/v1/payment-cards-cost -d "card_number=517862543698"
```

## Service Clearingcost (Persisted Clearing Cost Matrix table)
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

### TODO: ritesta docker
### TODO: rimetti il databin che vogliono

to run the services in your local java environment for development / debugging, in the main dir:
```

mvn -pl summer liberty:run
mvn -pl clearingcost liberty:run

mvn package # to build the war
docker-compose up # to start
```

to run a service so that you can change the code

```
mvn -pl summer liberty:dev
mvn -pl clearingcost liberty:dev

mvn -pl summer liberty:devc # runs in docker
mvn -pl clearingcost liberty:devc # runs in docker

```
