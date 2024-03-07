package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.query_dsl.Operator
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import co.elastic.clients.json.JsonData
import no.digdir.fdk.searchservice.model.*
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Service
import kotlin.math.ceil
import kotlin.math.roundToLong
import co.elastic.clients.elasticsearch._types.query_dsl.Query as DSLQuery

@Service
class SearchService(
    private val elasticSearchOperations: ElasticsearchOperations
) {
    fun search(search: SearchOperation, searchTypes: List<SearchType>?): SearchResult =
        elasticSearchOperations
            .search(
                search.toElasticQuery(searchTypes),
                SearchObject::class.java,
                IndexCoordinates.of(SEARCH_INDEX_NAME)
            )
            .toSearchResult(search.pagination)

    private fun SearchOperation.toElasticQuery(searchTypes: List<SearchType>?): Query {
        val builder = NativeQuery.builder()
                .withPageable(pagination.toPageable())

        if (sort != null) builder.addSorting(sort)

        if (query.isNullOrBlank()) builder.addEmptyQueryWithFilters(filters, searchTypes)
        else builder.addFilteredQuery(fields, query, filters, searchTypes)

        return builder.build()
    }

    private fun Pagination.toPageable(): Pageable =
        Pageable.ofSize(getSize())
            .withPage(getPage())

    private fun NativeQueryBuilder.addFilteredQuery(
        queryFields: QueryFields,
        queryValue: String,
        filters: SearchFilters?,
        searchTypes: List<SearchType>?
    ) {
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
                boolBuilder.filter(createQueryFilters(filters, searchTypes))
            }
        }
    }

    private fun NativeQueryBuilder.addEmptyQueryWithFilters(filters: SearchFilters?, searchTypes: List<SearchType>?) {
        withQuery { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.filter(createQueryFilters(filters, searchTypes))
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
            SortFieldEnum.FIRST_HARVESTED -> FilterFields.FirstHarvested.jsonPath()
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
                    .field(FilterFields.Deleted.jsonPath())
                    .value(FieldValue.of(false))
            }
        })

        searchTypes?.forEach { searchType ->
            queryFilters.add(
                DSLQuery.of { queryBuilder ->
                    queryBuilder.term { termBuilder ->
                        termBuilder
                            .field(FilterFields.SearchType.jsonPath())
                            .value(FieldValue.of(searchType.name))
                    }
                })
        }

        filters?.opendata?.let { opendata ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field(FilterFields.OpenData.jsonPath())
                        .value(FieldValue.of(opendata.value))
                }
            })
        }

        filters?.accessRights?.let { accessRights ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field(FilterFields.AccessRights.jsonPath())
                        .value(FieldValue.of(accessRights.value))
                }
            })
        }

        filters?.theme?.value?.let { themes ->
            themes.forEach { themeValue ->
                queryFilters.add(DSLQuery.of { queryBuilder ->
                    queryBuilder.term { termBuilder ->
                        termBuilder
                            .field(FilterFields.DataTheme.jsonPath())
                            .value(FieldValue.of(themeValue))
                    }
                })
            }
        }

        filters?.provenance?.let { provenance ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field(FilterFields.Provenance.jsonPath())
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
                            .field(FilterFields.Spatial.jsonPath())
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
                            .field(FilterFields.LosTheme.jsonPath())
                            .value(FieldValue.of(losValue))
                    }
                })
            }
        }

        filters?.orgPath?.let { orgPath ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field(FilterFields.OrgPath.jsonPath())
                        .value(FieldValue.of(orgPath.value))
                }
            })
        }

        filters?.formats?.value?.forEach { formatValue ->
                queryFilters.add(DSLQuery.of { queryBuilder ->
                    queryBuilder.match { matchBuilder ->
                        matchBuilder
                            .field(FilterFields.Format.jsonPath())
                            .query(FieldValue.of(formatValue))
                    }
                })
            }

        filters?.relations?.let { relation ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field(FilterFields.Relations.jsonPath())
                        .value(FieldValue.of(relation.value))
                }
            })
        }

        filters?.last_x_days?.let { daysAgo ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.range { rangeBuilder ->
                    rangeBuilder
                        .field(FilterFields.FirstHarvested.jsonPath())
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

    private fun SearchHits<SearchObject>.toSearchResult(
        pagination: Pagination
    ): SearchResult =
        map { it.content }
            .toList()
            .let {
                SearchResult(
                    hits = it,
                    page = PageMeta(
                        currentPage = pagination.getPage(),
                        size = it.size,
                        totalElements = totalHits,
                        totalPages = ceil(totalHits.toDouble() / pagination.getSize()).roundToLong()
                    )
                )
            }

    private fun FilterFields.jsonPath(): String = when(this) {
        FilterFields.AccessRights -> "accessRights.code.keyword"
        FilterFields.DataTheme -> "dataTheme.code.keyword"
        FilterFields.Deleted -> "metadata.deleted"
        FilterFields.FirstHarvested -> "metadata.firstHarvested"
        FilterFields.Format -> "fdkFormatPrefixed.keyword"
        FilterFields.LosTheme -> "losTheme.losPaths.keyword"
        FilterFields.OpenData -> "isOpenData"
        FilterFields.OrgPath -> "organization.orgPath.keyword"
        FilterFields.Provenance -> "provenance.code.keyword"
        FilterFields.Relations -> "relations.uri.keyword"
        FilterFields.SearchType -> "searchType.keyword"
        FilterFields.Spatial -> "spatial.prefLabel.nb.keyword"
    }

}

private enum class FilterFields {
    AccessRights, DataTheme, Deleted, FirstHarvested, Format, LosTheme,
    OpenData, OrgPath, Provenance, Relations, SearchType, Spatial
}
