# chaincost
Credit Card Cost API exercise 

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
the data storage should be per microservice!
(or not)

# MSSAPI "Microservices API"

endpoint /payment-cards-cost 
1 - the full card number is invoked to the endpoint  
2 - the system in turns invokes https://bintable.com/get-api with only a part of the number (IIN) and gets the card issuing country (using an internal cache to avoid useless calls) 
3 - the card issuing country code is matched with the clearing cost matrix to know and return the clearing cost 

endpoint(s) managing clearing cost matrix table
- Create (new country <--> clearing cost item)
- Update 
- Delete
- Read (returns cost for a country)


TODO: rimuovi web-inf, ecc