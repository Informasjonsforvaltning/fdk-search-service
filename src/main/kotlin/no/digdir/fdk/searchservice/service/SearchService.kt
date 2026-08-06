package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsAggregate
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate
import co.elastic.clients.elasticsearch._types.query_dsl.Operator
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import io.micrometer.core.instrument.Metrics
import no.digdir.fdk.searchservice.model.BucketCount
import no.digdir.fdk.searchservice.model.PageMeta
import no.digdir.fdk.searchservice.model.Pagination
import no.digdir.fdk.searchservice.model.QueryFields
import no.digdir.fdk.searchservice.model.SEARCH_INDEX_NAME
import no.digdir.fdk.searchservice.model.SearchFilters
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.model.SearchProfile
import no.digdir.fdk.searchservice.model.SearchResult
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.SortDirection
import no.digdir.fdk.searchservice.model.SortField
import no.digdir.fdk.searchservice.model.SortFieldEnum
import no.digdir.fdk.searchservice.model.toPageable
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.core.AggregationsContainer
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Component
import kotlin.math.ceil
import kotlin.math.roundToLong
import kotlin.time.measureTimedValue
import kotlin.time.toJavaDuration
import co.elastic.clients.elasticsearch._types.query_dsl.Query as DSLQuery

@Component
class SearchService(
    private val elasticSearchOperations: ElasticsearchOperations,
) {
    fun search(
        search: SearchOperation,
        searchTypes: List<SearchType>?,
    ): SearchResult {
        val (result, timeElapsed) =
            measureTimedValue {
                elasticSearchOperations
                    .search(
                        search.toElasticQuery(searchTypes),
                        SearchObject::class.java,
                        IndexCoordinates.of(SEARCH_INDEX_NAME),
                    ).toSearchResult(search.pagination ?: Pagination())
            }
        Metrics.timer("search").record(timeElapsed.toJavaDuration())
        return result
    }

    private fun SearchOperation.toElasticQuery(searchTypes: List<SearchType>?): Query {
        val queryFields = fields ?: QueryFields()
        val pageable = pagination?.toPageable() ?: Pagination().toPageable()
        val builder =
            NativeQuery
                .builder()
                .withPageable(pageable)
                .addAggregations()

        if (sort != null) builder.addSorting(sort)

        if (query.isNullOrBlank()) {
            builder.addEmptyQueryWithFilters(filters, searchTypes, profile)
        } else {
            builder.addFilteredQuery(queryFields, query, filters, searchTypes, profile)
        }

        return builder.build()
    }

    private fun NativeQueryBuilder.addFilteredQuery(
        queryFields: QueryFields,
        queryValue: String,
        filters: SearchFilters?,
        searchTypes: List<SearchType>?,
        profile: SearchProfile?,
    ) {
        withQuery { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.should {
                    it.multiMatch { matchBuilder ->
                        matchBuilder
                            .fields(queryFields.matchPaths(titleBoost = PREFIX_MATCH_TITLE_BOOST))
                            .query(queryValue)
                            .operator(Operator.And)
                            .type(TextQueryType.BoolPrefix)
                    }
                }

                boolBuilder.should {
                    it.multiMatch { matchBuilder ->
                        matchBuilder
                            .fields(queryFields.matchPaths(titleBoost = PHRASE_MATCH_TITLE_BOOST))
                            .query(queryValue)
                            .operator(Operator.And)
                            .type(TextQueryType.Phrase)
                    }
                }
                boolBuilder.minimumShouldMatch("1")
                boolBuilder.filter(createQueryFilters(filters, searchTypes, profile))
                boolBuilder.mustNot(createNullFilters(filters))
            }
        }
    }

    private fun NativeQueryBuilder.addEmptyQueryWithFilters(
        filters: SearchFilters?,
        searchTypes: List<SearchType>?,
        profile: SearchProfile?,
    ) {
        withQuery { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.filter(createQueryFilters(filters, searchTypes, profile))
                boolBuilder.mustNot(createNullFilters(filters))
            }
        }
    }

    private fun NativeQueryBuilder.addAggregations(): NativeQueryBuilder {
        addTermsAggregation(FilterFields.AccessRights, withMissingValue = true)
        addTermsAggregation(FilterFields.DataTheme)
        addTermsAggregation(FilterFields.Format)
        addTermsAggregation(FilterFields.LosTheme)
        addTermsAggregation(FilterFields.OrgPath, withMissingValue = true)
        addTermsAggregation(FilterFields.OpenData)
        addTermsAggregation(FilterFields.Provenance)
        addTermsAggregation(FilterFields.Spatial)

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
        profile: SearchProfile?,
    ): List<DSLQuery> {
        val queryFilters = commonQueryFilters(searchTypes, profile)

        filters?.openData?.value?.let { queryFilters.add(termFilter(FilterFields.OpenData, it)) }
        filters?.accessRights?.value?.let { queryFilters.add(termFilter(FilterFields.AccessRights, it)) }
        filters?.dataTheme?.value?.forEach { queryFilters.add(termFilter(FilterFields.DataTheme, it)) }
        filters?.provenance?.value?.let { queryFilters.add(termFilter(FilterFields.Provenance, it)) }
        filters?.spatial?.value?.forEach { queryFilters.add(termFilter(FilterFields.Spatial, it)) }
        filters?.losTheme?.value?.forEach { queryFilters.add(termFilter(FilterFields.LosTheme, it)) }
        filters?.orgPath?.value?.let { queryFilters.add(termFilter(FilterFields.OrgPath, it)) }
        filters?.formats?.value?.forEach { queryFilters.add(matchFilter(FilterFields.Format, it)) }
        filters?.relations?.value?.let { queryFilters.add(termFilter(FilterFields.Relations, it)) }
        filters?.lastXDays?.value?.let { queryFilters.add(rangeFromDaysAgoFilter(FilterFields.FirstHarvested, it)) }
        filters?.lastXDaysModified?.value?.let { queryFilters.add(rangeFromDaysAgoFilter(FilterFields.Modified, it)) }
        filters?.uri?.value?.let { queryFilters.add(termsFilter(FilterFields.Uri, it)) }

        return queryFilters
    }

    /**
     * Filters that ask for the *absence* of a field: when a filter is supplied with a `null`
     * value, that's interpreted as "only match documents where this field is missing".
     */
    private fun createNullFilters(filters: SearchFilters?): List<DSLQuery> {
        val queryFilters = mutableListOf<DSLQuery>()

        if (filters?.accessRights != null && filters.accessRights.value == null) {
            queryFilters.add(existsFilter(FilterFields.AccessRights))
        }

        if (filters?.orgPath != null && filters.orgPath.value == null) {
            queryFilters.add(existsFilter(FilterFields.OrgPath))
        }

        return queryFilters
    }

    private fun QueryFields.matchPaths(titleBoost: Int): List<String> =
        listOf(
            if (title != false) {
                languagePaths("title", titleBoost)
            } else {
                emptyList()
            },
            if (description != false) {
                languagePaths("description")
            } else {
                emptyList()
            },
            if (keyword != false) {
                languagePaths("keyword", 5)
            } else {
                emptyList()
            },
            if (additionalTitles != false) {
                languagePaths("additionalTitles", 10)
            } else {
                emptyList()
            },
        ).flatten()

    private fun languagePaths(
        basePath: String,
        boost: Int? = null,
    ): List<String> =
        listOf(
            "$basePath.nb${if (boost != null) "^$boost" else ""}",
            "$basePath.nn${if (boost != null) "^$boost" else ""}",
            "$basePath.no${if (boost != null) "^$boost" else ""}",
            "$basePath.en${if (boost != null) "^$boost" else ""}",
        )

    private fun StringTermsAggregate.toBucketCounts(): List<BucketCount> =
        buckets().array().map {
            BucketCount(
                key = it.key().stringValue(),
                count = it.docCount(),
            )
        }

    private fun LongTermsAggregate.toOpenDataCounts(): List<BucketCount> =
        buckets().array().map {
            BucketCount(
                key = it.keyAsString() ?: "null",
                count = it.docCount(),
            )
        }

    private fun Aggregate.toBucketCounts(aggregateName: String): List<BucketCount> =
        when (aggregateName) {
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
        return aggregations
            .map { it.aggregation() }
            .associate { it.name to it.aggregate.toBucketCounts(it.name) }
    }

    private fun SearchHits<SearchObject>.toSearchResult(pagination: Pagination): SearchResult =
        map { it.content }
            .toList()
            .let {
                SearchResult(
                    hits = it,
                    aggregations = aggregations?.toAggregationCounts() ?: emptyMap(),
                    page =
                        PageMeta(
                            currentPage = pagination.getPage(),
                            size = it.size,
                            totalElements = totalHits,
                            totalPages = ceil(totalHits.toDouble() / pagination.getSize()).roundToLong(),
                        ),
                )
            }
}

private const val PREFIX_MATCH_TITLE_BOOST = 15
private const val PHRASE_MATCH_TITLE_BOOST = 30
