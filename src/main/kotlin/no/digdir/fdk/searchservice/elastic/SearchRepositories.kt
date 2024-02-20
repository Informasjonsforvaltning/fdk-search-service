package no.digdir.fdk.searchservice.elastic

import no.digdir.fdk.searchservice.model.Concept
import no.digdir.fdk.searchservice.model.Dataset
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface DatasetSearchRepository : ElasticsearchRepository<Dataset, String>
interface ConceptSearchRepository : ElasticsearchRepository<Concept, String>
