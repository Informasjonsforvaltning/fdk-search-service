package no.digdir.fdk.searchservice.elastic

import no.digdir.fdk.searchservice.model.Concept
import no.digdir.fdk.searchservice.model.Dataservice
import no.digdir.fdk.searchservice.model.Dataset
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface DatasetSearchRepository : ElasticsearchRepository<Dataset, String>

@Repository
interface ConceptSearchRepository : ElasticsearchRepository<Concept, String>

@Repository
interface DataserviceSearchRepository : ElasticsearchRepository<Dataservice, String>
