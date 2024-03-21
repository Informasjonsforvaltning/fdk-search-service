package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsAggregate
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate
import co.elastic.clients.elasticsearch._types.query_dsl.Operator
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import co.elastic.clients.json.JsonData
import no.digdir.fdk.searchservice.model.*
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.core.AggregationsContainer
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
            .addAggregations()

        if (sort != null) builder.addSorting(sort)

        if (query.isNullOrBlank()) builder.addEmptyQueryWithFilters(filters, searchTypes, profile)
        else builder.addFilteredQuery(fields, query, filters, searchTypes, profile)

        return builder.build()
    }

    private fun NativeQueryBuilder.addFilteredQuery(
        queryFields: QueryFields,
        queryValue: String,
        filters: SearchFilters?,
        searchTypes: List<SearchType>?,
        profile: SearchProfile?
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
                boolBuilder.filter(createQueryFilters(filters, searchTypes, profile))
            }
        }
    }

    private fun NativeQueryBuilder.addEmptyQueryWithFilters(
        filters: SearchFilters?,
        searchTypes: List<SearchType>?,
        profile: SearchProfile?
    ) {
        withQuery { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.filter(createQueryFilters(filters, searchTypes, profile))
            }
        }
    }

    private fun NativeQueryBuilder.addAggregations(): NativeQueryBuilder {
        withAggregation(
            FilterFields.AccessRights.aggregationName(),
            AggregationBuilders.terms { builder ->
                builder.field(FilterFields.AccessRights.jsonPath())
            }
        )
        withAggregation(
            FilterFields.DataTheme.aggregationName(),
            AggregationBuilders.terms { builder ->
                builder.field(FilterFields.DataTheme.jsonPath())
            }
        )
        withAggregation(
            FilterFields.Format.aggregationName(),
            AggregationBuilders.terms { builder ->
                builder.field(FilterFields.Format.jsonPath())
            }
        )
        withAggregation(
            FilterFields.LosTheme.aggregationName(),
            AggregationBuilders.terms { builder ->
                builder.field(FilterFields.LosTheme.jsonPath())
            }
        )
        withAggregation(
            FilterFields.OrgPath.aggregationName(),
            AggregationBuilders.terms { builder ->
                builder.field(FilterFields.OrgPath.jsonPath())
            }
        )
        withAggregation(
            FilterFields.OpenData.aggregationName(),
            AggregationBuilders.terms { builder ->
                builder.field(FilterFields.OpenData.jsonPath())
            }
        )
        withAggregation(
            FilterFields.Provenance.aggregationName(),
            AggregationBuilders.terms { builder ->
                builder.field(FilterFields.Provenance.jsonPath())
            }
        )
        withAggregation(
            FilterFields.Spatial.aggregationName(),
            AggregationBuilders.terms { builder ->
                builder.field(FilterFields.Spatial.jsonPath())
            }
        )

        return this
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

    private fun createQueryFilters(
        filters: SearchFilters?,
        searchTypes: List<SearchType>?,
        profile: SearchProfile?
    ): List<DSLQuery> {
        val queryFilters = mutableListOf<DSLQuery>()

        queryFilters.add(DSLQuery.of { queryBuilder ->
            queryBuilder.term { termBuilder ->
                termBuilder
                    .field(FilterFields.Deleted.jsonPath())
                    .value(FieldValue.of(false))
            }
        })

        if (searchTypes != null) {
            queryFilters.add(
                DSLQuery.of { queryBuilder ->
                    queryBuilder.terms { termsBuilder ->
                        termsBuilder
                            .field(FilterFields.SearchType.jsonPath())
                            .terms { termsQueryBuilder ->
                                termsQueryBuilder
                                    .value(searchTypes.map { FieldValue.of(it.name) })
                            }
                    }
                })
        }

        filters?.openData?.let { opendata ->
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

        filters?.dataTheme?.value?.let { themes ->
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
            spatial.value.forEach { spatialValue ->
                queryFilters.add(DSLQuery.of { queryBuilder ->
                    queryBuilder.term { termBuilder ->
                        termBuilder
                            .field(FilterFields.Spatial.jsonPath())
                            .value(FieldValue.of(spatialValue))
                    }
                })
            }
        }

        filters?.losTheme?.let { los ->
            los.value.forEach { losValue ->
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

        filters?.lastXDays?.let { daysAgo ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.range { rangeBuilder ->
                    rangeBuilder
                        .field(FilterFields.FirstHarvested.jsonPath())
                        .gte(JsonData.of("now-${daysAgo.value}d/d"))
                }
            })
        }

        filters?.uri?.value?.forEach { uriValue ->
            queryFilters.add(DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field(FilterFields.Uri.jsonPath())
                        .value(FieldValue.of(uriValue))
                }
            })
        }

        if (profile == SearchProfile.TRANSPORT) queryFilters.add(filtersForProfile(profile))

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
            "$basePath.no${if (boost != null) "^$boost" else ""}",
            "$basePath.en${if (boost != null) "^$boost" else ""}")

    private fun StringTermsAggregate.toBucketCounts(): List<BucketCount> =
        buckets().array().map {
            BucketCount(
                key = it.key().stringValue(),
                count = it.docCount()
            )
        }

    private fun LongTermsAggregate.toOpenDataCounts(): List<BucketCount> =
        buckets().array().map {
            BucketCount(
                key = it.keyAsString() ?: "null",
                count = it.docCount()
            )
        }

    private fun Aggregate.toBucketCounts(aggregateName: String): List<BucketCount> =
        when(aggregateName) {
            FilterFields.AccessRights.aggregationName() -> (_get() as StringTermsAggregate).toBucketCounts()
            FilterFields.DataTheme.aggregationName() -> (_get() as StringTermsAggregate).toBucketCounts()
            FilterFields.Format.aggregationName() -> (_get() as StringTermsAggregate).toBucketCounts()
            FilterFields.LosTheme.aggregationName() -> (_get() as StringTermsAggregate).toBucketCounts()
            FilterFields.OrgPath.aggregationName() -> (_get() as StringTermsAggregate).toBucketCounts()
            FilterFields.OpenData.aggregationName() -> (_get() as LongTermsAggregate).toOpenDataCounts()
            FilterFields.Provenance.aggregationName() -> (_get() as StringTermsAggregate).toBucketCounts()
            FilterFields.Spatial.aggregationName() -> (_get() as StringTermsAggregate).toBucketCounts()
            else -> emptyList()
        }

    private fun AggregationsContainer<*>.toAggregationCounts(): Map<String, List<BucketCount>> {
        val aggregations = aggregations() as List<ElasticsearchAggregation>
        return aggregations.map { it.aggregation() }
            .associate { it.name to it.aggregate.toBucketCounts(it.name) }
    }

    private fun SearchHits<SearchObject>.toSearchResult(
        pagination: Pagination
    ): SearchResult =
        map { it.content }
            .toList()
            .let {
                SearchResult(
                    hits = it,
                    aggregations = aggregations.toAggregationCounts(),
                    page = PageMeta(
                        currentPage = pagination.getPage(),
                        size = it.size,
                        totalElements = totalHits,
                        totalPages = ceil(totalHits.toDouble() / pagination.getSize()).roundToLong()
                    )
                )
            }

    private val transportProfileLosValues =
        listOf(
            FieldValue.of("trafikk-og-transport/mobilitetstilbud"),
            FieldValue.of("trafikk-og-transport/trafikkinformasjon"),
            FieldValue.of("trafikk-og-transport/veg-og-vegregulering"),
            FieldValue.of("trafikk-og-transport/yrkestransport"),
        )

    private fun filtersForProfile(profile: SearchProfile) = when(profile) {
        SearchProfile.TRANSPORT -> {
            co.elastic.clients.elasticsearch._types.query_dsl.Query.of { queryBuilder ->
                queryBuilder.terms { termsBuilder ->
                    termsBuilder
                        .field(FilterFields.LosTheme.jsonPath())
                        .terms { termsQueryBuilder ->
                            termsQueryBuilder.value(transportProfileLosValues)
                        }
                }
            }
        }
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
        FilterFields.Uri -> "uri.keyword"
    }

    private fun FilterFields.aggregationName(): String = when(this) {
        FilterFields.AccessRights -> "accessRights"
        FilterFields.DataTheme -> "dataTheme"
        FilterFields.Deleted -> "deleted"
        FilterFields.FirstHarvested -> "firstHarvested"
        FilterFields.Format -> "format"
        FilterFields.LosTheme -> "losTheme"
        FilterFields.OpenData -> "openData"
        FilterFields.OrgPath -> "orgPath"
        FilterFields.Provenance -> "provenance"
        FilterFields.Relations -> "relations"
        FilterFields.SearchType -> "searchType"
        FilterFields.Spatial -> "spatial"
        FilterFields.Uri -> "uri"
    }

}

private enum class FilterFields {
    AccessRights, DataTheme, Deleted, FirstHarvested, Format, LosTheme,
    OpenData, OrgPath, Provenance, Relations, SearchType, Spatial, Uri
}
