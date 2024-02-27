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
    fun search(search: SearchOperation, searchTypes: List<SearchType>?): List<SearchObject> =
        elasticSearchOperations.search(
            search.toElasticQuery(searchTypes),
            SearchObject::class.java,
            IndexCoordinates.of(SEARCH_INDEX_NAME)
        ).toSearchObjectList()

    private fun SearchOperation.toElasticQuery(searchTypes: List<SearchType>?): Query {
        val builder = NativeQuery.builder()
        if (!query.isNullOrBlank()) builder.addFieldsQuery(query)
        builder.addFilters(filters, searchTypes)
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

    private fun NativeQueryBuilder.addFilters(filters: SearchFilters?, searchTypes: List<SearchType>?) {
        withFilter { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.must(createQueryFilters(filters, searchTypes))
            }
        }
    }

    private fun createQueryFilters(filters: SearchFilters?, searchTypes: List<SearchType>?): List<DSLQuery> {
        val queryFilters = mutableListOf<DSLQuery>()

        queryFilters.add(DSLQuery.of { queryBuilder ->
            queryBuilder.term { termBuilder ->
                termBuilder
                    .field("metadata.deleted")
                    .value(FieldValue.of(false))
            }
        })

        searchTypes?.forEach { searchTypes ->
            queryFilters.add(
                DSLQuery.of { queryBuilder ->
                    queryBuilder.term { termBuilder ->
                        termBuilder
                            .field("searchType.keyword")
                            .value(FieldValue.of(searchTypes.name))
                    }
                })
        }

        filters?.opendata?.let { opendata ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("isOpenData")
                        .value(FieldValue.of(opendata.value))
                }
            })
        }

        filters?.accessRights?.let { accessRights ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("accessRights.code.keyword")
                        .value(FieldValue.of(accessRights.value))
                }
            })
        }

        filters?.theme?.value?.let { themes ->
            themes.forEach { themeValue ->
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
                        .value(FieldValue.of(provenance.value))
                }
            })
        }

        filters?.spatial?.let { spatial ->
            val filterList = spatial.value.split(",").map { it.trim() }

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
            val losThemeList = los.value.split(",").map { it.trim() }

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
                        .value(FieldValue.of(orgPath.value))
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

        filters?.relations?.let { relation ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("relations.uri.keyword")
                        .value(relation)
                }
            })
        }

        return queryFilters
    }

    private fun SearchHits<SearchObject>.toSearchObjectList(): List<SearchObject> = this.map { it.content }.toList()
}
