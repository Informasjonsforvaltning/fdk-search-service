# FDK Search Service

This application provides an API for searching all resources (datasets, concepts etc.). The service consumes RDF parse
events (Kafka) and indexing these resources as documents (`SearchObject`) in ElasticSearch.

For a broader understanding of the system’s context, refer to
the [architecture documentation](https://github.com/Informasjonsforvaltning/architecture-documentation) wiki. For more
specific context on this application, see the **Portal** subsystem section.

## Getting Started

These instructions will give you a copy of the project up and running on your local machine for development and testing
purposes.

### Prerequisites

Ensure you have the following installed:

- Java 21
- Maven
- Docker

### Running locally

#### Clone the repository

```sh
git clone https://github.com/Informasjonsforvaltning/fdk-search-service.git
cd fdk-search-service
```

#### Generate sources

Kafka messages are serialized using Avro. Avro schemas are located in ```kafka/schemas```. To generate sources from Avro
schema, run the following command:

```
mvn generate-sources    
```

#### Start Elasticsearch, Kafka cluster and setup topics/schemas

Topics and schemas are set up automatically when starting the Kafka cluster. Docker compose uses the scripts
```create-topics.sh``` and ```create-schemas.sh``` to set up topics and schemas.

```
docker-compose up -d
```

If you have problems starting kafka, check if all health checks are ok. Make sure number at the end (after 'grep')
matches desired topics.

#### Start application

```
mvn spring-boot:run -Dspring-boot.run.profiles=develop
```

#### Produce messages

Check if schema id is correct in the script. This should be 1 if there is only one schema in your registry.

```
sh ./kafka/produce-messages.sh
```

### API Documentation (OpenAPI)

The API documentation is available at ```openapi.yaml```.

### Running tests

```sh
mvn verify
```

## About

### The general `SearchObject` model

The generalized model is used to represent all resources (datasets, concepts etc.), with fields that are common to all
resources, and used for searching.

### The `SearchResult` model

The result of search queries is represented by the `SearchResult` model. This model contains paginated list of
`SearchObject`.

## Examples

### Search examples

#### Searching all resources:

```
URL: https://search.api.staging.fellesdatakatalog.digdir.no/search
```

#### Searching specific resources:

```
URL: https://search.api.staging.fellesdatakatalog.digdir.no/search/[resourceType]
```

#### For example:

```
URL: https://search.api.staging.fellesdatakatalog.digdir.no/search/datasets
```

### Different Payloads:

#### Search with query:

Payload:

```
{
    "query": "test",
}
```

#### Search with pagination:

Payload:

```
{
    "pagination": {
        "size": 5,
        "page": 2
    }
}
```

#### Search with sorting:

Payload:

```
{
    "sort": {
        "field": "FIRST_HARVESTED",
        "direction": "DESC"
    }
}
```

#### Search with filters:

Payload:

```
{
    "filters": {
        "accessRights": {
            "value": "RESTRICTED"
        },
        "dataTheme": {
            "value": [
                "REGI"
            ]
        },
        "losTheme": {
            "value": [
                "familie-og-barn"
            ]
        }
    }
}
```
