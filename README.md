# Chaincost
Credit Card Cost API exercise 

1 - the full card number is invoked to the endpoint  
2 - the system in turns invokes https://bintable.com/get-api with only a part of the number (IIN) and gets the card issuing country (using an internal cache to avoid useless calls) 
3 - the card issuing country code is matched with the clearing cost matrix to know and return the clearing cost 



# MSSAPI "Microservices API"
### Endpoint /payment-cards-cost 

```
curl -Ss -X POST http://localhost:9081/summer/api/v1/payment-cards-cost -d "card_number=517862543698"
```

the data storage for the clearig cost matrix is a serialized map, internal to the microservice clearingcost

## Endpoint(s) managing the clearing cost matrix table
- Create (new country <--> clearing cost item)
- Update 
- Delete
- Read (returns cost for a country)

### GET method on main endpoint
```
curl -v http://localhost:9080/clearingcost/|jq
```

### GET method on main country endpoint
```
curl -v http://localhost:9080/clearingcost/gr|jq
```

### PUT method
```
curl -vX PUT http://localhost:9080/clearingcost -H "content-type:application/json" --data-raw '{"country": "fr", "cost": "113"}'
```

### DELETE method
```
curl -vX DELETE http://localhost:9080/clearingcost/GB
```



### TODO: rimuovi web-inf, ecc
### TODO: rimuovi copyrights
### TODO: ricorda che se la nazione none è presente, il costo è 10

to run the services in your local java environment for development / debugging, in the main dir:
```

mvn -pl inventory liberty:run
mvn -pl clearingcost liberty:run

mvn package # to build the war
docker-compose up # to start
```

to run a service so that you can change the code

```
mvn -pl inventory liberty:dev
mvn -pl clearingcost liberty:dev

mvn -pl inventory liberty:devc # runs in docker
mvn -pl clearingcost liberty:devc # runs in docker

```