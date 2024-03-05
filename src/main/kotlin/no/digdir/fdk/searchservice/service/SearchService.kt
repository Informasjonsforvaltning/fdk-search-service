package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.query_dsl.Operator
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import co.elastic.clients.json.JsonData
import no.digdir.fdk.searchservice.model.*
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Service
import java.time.Instant
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

        builder.addFilters(filters, searchTypes)

        if (sort != null) builder.addSorting(sort)

        if (!query.isNullOrBlank()) builder.addFieldsQuery(fields, query)


        return builder.build()
    }

    private fun NativeQueryBuilder.addFilters(filters: SearchFilters?, searchTypes: List<SearchType>?) {
        withFilter { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.must(createQueryFilters(filters, searchTypes))
            }
        }
    }

    private fun NativeQueryBuilder.addFieldsQuery(queryFields: QueryFields, queryValue: String) {
        withQuery { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.should {
                    it.multiMatch { matchBuilder ->
                        matchBuilder
                            .fields(queryFields.prefixMatchPaths())
                            .query(queryValue)
                            .operator(Operator.And)
                            .type(TextQueryType.BoolPrefix)
                    }
                }

                boolBuilder.should {
                    it.multiMatch { matchBuilder ->
                        matchBuilder.fields(queryFields.phraseMatchPaths())
                            .query(queryValue)
                            .operator(Operator.And)
                            .type(TextQueryType.Phrase)
                    }
                }
                boolBuilder.minimumShouldMatch("1")
            }
        }
    }

    private fun NativeQueryBuilder.addSorting(sort: SortField) {
        withSort { sortBuilder ->
            sortBuilder.field { fieldBuilder ->
                fieldBuilder.field(sort.sortField()).order(sort.sortDirection())
            }
        }
    }

    private fun SortField.sortField(): String =
        when (field) {
            SortFieldEnum.FIRST_HARVESTED -> "metadata.firstHarvested"
        }

    private fun SortField.sortDirection(): SortOrder =
        when (direction) {
            SortDirection.ASC -> SortOrder.Asc
            else -> SortOrder.Desc
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

        searchTypes?.forEach { searchType ->
            queryFilters.add(
                DSLQuery.of { queryBuilder ->
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
                        .value(FieldValue.of(relation.value))
                }
            })
        }

        filters?.last_x_days?.let { daysAgo ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.range { rangeBuilder ->
                    rangeBuilder
                        .field("metadata.firstHarvested")
                        .gte(JsonData.of("now-${daysAgo.value}d/d"))
                }
            })
        }

        return queryFilters
    }

    private fun QueryFields.prefixMatchPaths(): List<String> =
        listOf(
            if (title) languagePaths("title", 15)
            else emptyList(),

            if (description) languagePaths("description")
            else emptyList(),

            if (keyword) languagePaths("keyword", 5)
            else emptyList(),

        ).flatten()

    private fun QueryFields.phraseMatchPaths(): List<String> =
        listOf(
            if (title) languagePaths("title", 30)
            else emptyList(),

            if (description) languagePaths("description")
            else emptyList(),

            if (keyword) languagePaths("keyword", 5)
            else emptyList(),

        ).flatten()

    private fun languagePaths(basePath: String, boost: Int? = null): List<String> =
        listOf("$basePath.nb${if (boost != null) "^$boost" else ""}",
            "$basePath.nn${if (boost != null) "^$boost" else ""}",
            "$basePath.en${if (boost != null) "^$boost" else ""}")

    private fun SearchHits<SearchObject>.toSearchObjectList(): List<SearchObject> = this.map { it.content }.toList()
}
