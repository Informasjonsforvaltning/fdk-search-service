package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.Operator
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import no.digdir.fdk.searchservice.config.DATASET_INDEX_NAME
import no.digdir.fdk.searchservice.model.Dataset
import no.digdir.fdk.searchservice.model.SearchFilters
import no.digdir.fdk.searchservice.model.SearchOperation
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Service
import co.elastic.clients.elasticsearch._types.query_dsl.Query as DSLQuery

@Service
class DatasetSearchService(
    private val elasticSearchOperations: ElasticsearchOperations
) {
    fun searchDatasets(search: SearchOperation): List<Dataset> =
        elasticSearchOperations.search(
            search.toElasticQuery(),
            Dataset::class.java,
            IndexCoordinates.of(DATASET_INDEX_NAME)
        ).toDatasetList()

    private fun SearchOperation.toElasticQuery(): Query {
        val builder = NativeQuery.builder()
        if (!query.isNullOrBlank()) builder.addFieldsQuery(query)
        if (filters != null) builder.addFilters(filters)
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

    private fun NativeQueryBuilder.addFilters(filters: SearchFilters) {
        withFilter { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.must(filters.asQueryFilters())
            }
        }
    }

    private fun SearchHits<Dataset>.toDatasetList(): List<Dataset> = this.map { it.content }.toList()

    private fun SearchFilters.asQueryFilters(): List<DSLQuery> {
        val queryFilters = mutableListOf<DSLQuery>()

        if (opendata != null) {
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("isOpenData")
                        .value(FieldValue.of(opendata))
                }
            })
        }

        if (accessRights != null) {
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("accessRights.code.keyword")
                        .value(FieldValue.of(accessRights))
                }
            })
        }
        return queryFilters
    }
}
