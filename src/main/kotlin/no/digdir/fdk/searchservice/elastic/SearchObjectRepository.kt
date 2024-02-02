package no.digdir.fdk.searchservice.elastic

import no.digdir.fdk.searchservice.model.Dataset
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface SearchObjectRepository : ElasticsearchRepository<Dataset, String>
