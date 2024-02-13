package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.query_dsl.Operator
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import no.digdir.fdk.searchservice.config.INFORMATION_MODEL_INDEX_NAME
import no.digdir.fdk.searchservice.model.InformationModel
import no.digdir.fdk.searchservice.model.SearchOperation
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Service

@Service
class InformationModelSearchService(
    private val elasticSearchOperations: ElasticsearchOperations
) {
    fun searchInformationModels(search: SearchOperation): List<InformationModel> =
        elasticSearchOperations.search(
            search.toElasticQuery(),
            InformationModel::class.java,
            IndexCoordinates.of(INFORMATION_MODEL_INDEX_NAME)
        ).toInformationModelList()

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

    private fun SearchHits<InformationModel>.toInformationModelList(): List<InformationModel> = this.map { it.content }.toList()
}
