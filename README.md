# fdk-search-service
This service is responsible for searching for all resources in FDK. The service consumes RDF parse events (Kafka) 
and indexing these resources as documents (`SearchObject`s) in Elastic Search.

### About the general `SearchObject` model
The generalized model is used to represent all resources in FDK, with fields that are common to all
resources, and used for searching.

### About the `SearchResult` model
The result of search queries is represented by the `SearchResult` model. This model contains paginated list of 
`SearchObject`s.


## Requirements
- maven
- java 21
- docker
- docker-compose

## Generate sources
```
mvn generate-sources    
```

## Run tests
```
mvn test
```

## Run locally

### Start Kafka cluster and setup topics/schemas
Topics and schemas are setup automatically when starting the Kafka cluster.
Docker compose uses the scripts create-topics.sh and create-schemas.sh to setup topics and schemas.
```
docker-compose up -d
```
If you have problems starting kafka, check if all health checks are ok.
Make sure number at the end (after 'grep') matches desired topics.

### Start search service
Start search service locally using maven. Use Spring profile **develop**.
```
mvn spring-boot:run -Dspring-boot.run.profiles=develop
```

### Produce messages
Check if schema id is correct in the produce-messages.sh script. This should be 1 if there
is only one schema in your registry.
```
sh ./kafka/produce-messages.sh
```
