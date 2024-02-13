package no.digdir.fdk.searchservice.elastic

import no.digdir.fdk.searchservice.model.*
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface DatasetSearchRepository : ElasticsearchRepository<Dataset, String>

@Repository
interface ConceptSearchRepository : ElasticsearchRepository<Concept, String>

@Repository
interface DataserviceSearchRepository : ElasticsearchRepository<Dataservice, String>

@Repository
interface InformationModelSearchRepository : ElasticsearchRepository<InformationModel, String>

@Repository
interface EventSearchRepository : ElasticsearchRepository<Event, String>
