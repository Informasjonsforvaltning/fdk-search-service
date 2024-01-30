package no.digdir.fdk.searchservice.elastic

import no.digdir.fdk.searchservice.model.TempTestDBO
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface SearchObjectRepository : ElasticsearchRepository<TempTestDBO, String>