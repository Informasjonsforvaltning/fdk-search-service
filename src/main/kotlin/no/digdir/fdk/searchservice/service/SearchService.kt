package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.Operator
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import no.digdir.fdk.searchservice.model.*
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Service
import co.elastic.clients.elasticsearch._types.query_dsl.Query as DSLQuery

@Service
class SearchService(
    private val elasticSearchOperations: ElasticsearchOperations
) {
    fun search(search: SearchOperation, searchType: SearchType?): List<SearchObject> =
        elasticSearchOperations.search(
            search.toElasticQuery(searchType),
            SearchObject::class.java,
            IndexCoordinates.of(SEARCH_INDEX_NAME)
        ).toSearchObjectList()

    private fun SearchOperation.toElasticQuery(searchType: SearchType?): Query {
        val builder = NativeQuery.builder()
        if (!query.isNullOrBlank()) builder.addFieldsQuery(query)
        builder.addFilters(filters, searchType)
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

    private fun NativeQueryBuilder.addFilters(filters: SearchFilters?, searchType: SearchType?) {
        withFilter { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.must(createQueryFilters(filters, searchType))
            }
        }
    }

    private fun createQueryFilters(filters: SearchFilters?, searchType: SearchType?): List<DSLQuery> {
        val queryFilters = mutableListOf<DSLQuery>()

        queryFilters.add(DSLQuery.of { queryBuilder ->
            queryBuilder.term { termBuilder ->
                termBuilder
                        .field("metadata.deleted")
                        .value(FieldValue.of(false))
            }
        })
        if (searchType != null) {
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("searchType.keyword")
                        .value(FieldValue.of(searchType.name))
                }
            })
        }

        filters?.opendata?.let { opendata ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("isOpenData")
                        .value(FieldValue.of(opendata))
                }
            })
        }

        filters?.accessRights?.let { accessRights ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("accessRights.code.keyword")
                        .value(FieldValue.of(accessRights))
                }
            })
        }

        filters?.theme?.let { theme ->
            val themeList = theme.split(",").map { it.trim() }

            themeList.forEach { themeValue ->
                queryFilters.add(DSLQuery.of { queryBuilder ->
                    queryBuilder.term { termBuilder ->
                        termBuilder
                            .field("dataTheme.code.keyword")
                            .value(FieldValue.of(themeValue))
                    }
                })
            }
        }

        filters?.provenance?.let { provenance ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("provenance.code.keyword")
                        .value(FieldValue.of(provenance))
                }
            })
        }

        filters?.spatial?.let { spatial ->
            val filterList = spatial.split(",").map { it.trim() }

            filterList.forEach { spatialValue ->
                queryFilters.add(DSLQuery.of { queryBuilder ->
                    queryBuilder.term { termBuilder ->
                        termBuilder
                            .field("spatial.prefLabel.nb.keyword")
                            .value(FieldValue.of(spatialValue))
                    }
                })
            }
        }

        filters?.los?.let { los ->
            val losThemeList = los.split(",").map { it.trim() }

            losThemeList.forEach { losValue ->
                queryFilters.add(DSLQuery.of { queryBuilder ->
                    queryBuilder.term { termBuilder ->
                        termBuilder
                            .field("losTheme.losPaths.keyword")
                            .value(FieldValue.of(losValue))
                    }
                })
            }
        }
        filters?.orgPath?.let { orgPath ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("organization.orgPath.keyword")
                        .value(FieldValue.of(orgPath))
                }
            })
        }

        filters?.formats?.value?.forEach { formatValue ->
                queryFilters.add(DSLQuery.of { queryBuilder ->
                    queryBuilder.match { matchBuilder ->
                        matchBuilder
                            .field("fdkFormatPrefixed.keyword")
                            .query(FieldValue.of(formatValue))
                    }
                })
            }
            return queryFilters
    }
    private fun SearchHits<SearchObject>.toSearchObjectList(): List<SearchObject> = this.map { it.content }.toList()
}
