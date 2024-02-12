package no.digdir.fdk.searchservice.elastic

import no.digdir.fdk.searchservice.model.SearchObject
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface SearchRepository : ElasticsearchRepository<SearchObject, String>
