package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.query_dsl.Operator
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import no.digdir.fdk.searchservice.config.DATASERVICE_INDEX_NAME
import no.digdir.fdk.searchservice.model.Dataservice
import no.digdir.fdk.searchservice.model.SearchOperation
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Service

@Service
class DataserviceSearchService(
    private val elasticSearchOperations: ElasticsearchOperations
) {
    fun searchDataservices(search: SearchOperation): List<Dataservice> =
        elasticSearchOperations.search(
            search.toElasticQuery(),
            Dataservice::class.java,
            IndexCoordinates.of(DATASERVICE_INDEX_NAME)
        ).toDataserviceList()

    private fun SearchOperation.toElasticQuery(): Query {
        val builder = NativeQuery.builder()
        if (!query.isNullOrBlank()) builder.addFieldsQuery(query)
        return builder.build()
    }

    private fun NativeQueryBuilder.addFieldsQuery(queryValue: String) {
        withQuery { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.should {
                    it.multiMatch { matchBuilder ->
                        matchBuilder
                            .query(queryValue)
                            .operator(Operator.And)
                            .type(TextQueryType.BoolPrefix)
                    }
                }
            }
        }
    }

    private fun SearchHits<Dataservice>.toDataserviceList(): List<Dataservice> = this.map { it.content }.toList()
}
