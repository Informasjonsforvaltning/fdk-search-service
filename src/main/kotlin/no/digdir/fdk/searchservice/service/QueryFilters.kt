package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation
import no.digdir.fdk.searchservice.model.SearchFilters
import no.digdir.fdk.searchservice.model.SearchProfile
import no.digdir.fdk.searchservice.model.SearchType
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import co.elastic.clients.elasticsearch._types.query_dsl.Query as DSLQuery

/**
 * Shared building blocks for the Elasticsearch filters used by both [SearchService] and
 * [SuggestionService], so the two services only need to express what differs between them.
 */

internal enum class FilterFields {
    AccessRights,
    DataTheme,
    DcatProfiles,
    Deleted,
    FirstHarvested,
    Modified,
    Format,
    LosTheme,
    OpenData,
    OrgPath,
    OrgId,
    Provenance,
    Relations,
    SearchType,
    Spatial,
    Uri,
    TransportRelation,
}

internal fun FilterFields.jsonPath(): String = when (this) {
    FilterFields.AccessRights -> "accessRights.code.keyword"
    FilterFields.DataTheme -> "dataTheme.code.keyword"
    FilterFields.DcatProfiles -> "dcatProfiles.keyword"
    FilterFields.Deleted -> "metadata.deleted"
    FilterFields.FirstHarvested -> "metadata.firstHarvested"
    FilterFields.Modified -> "metadata.modified"
    FilterFields.Format -> "fdkFormatPrefixed.keyword"
    FilterFields.LosTheme -> "losTheme.losPaths.keyword"
    FilterFields.OpenData -> "isOpenData"
    FilterFields.OrgPath -> "organization.orgPath.keyword"
    FilterFields.OrgId -> "organization.id.keyword"
    FilterFields.Provenance -> "provenance.code.keyword"
    FilterFields.Relations -> "relations.uri.keyword"
    FilterFields.SearchType -> "searchType.keyword"
    FilterFields.Spatial -> "spatial.prefLabel.nb.keyword"
    FilterFields.Uri -> "uri.keyword"
    FilterFields.TransportRelation -> "isRelatedToTransportportal"
}

internal fun FilterFields.aggregationName(): String = when (this) {
    FilterFields.AccessRights -> "accessRights"
    FilterFields.DataTheme -> "dataTheme"
    FilterFields.DcatProfiles -> "dcatProfiles"
    FilterFields.Deleted -> "deleted"
    FilterFields.FirstHarvested -> "firstHarvested"
    FilterFields.Modified -> "modified"
    FilterFields.Format -> "format"
    FilterFields.LosTheme -> "losTheme"
    FilterFields.OpenData -> "openData"
    FilterFields.OrgPath -> "orgPath"
    FilterFields.OrgId -> "orgId"
    FilterFields.Provenance -> "provenance"
    FilterFields.Relations -> "relations"
    FilterFields.SearchType -> "searchType"
    FilterFields.Spatial -> "spatial"
    FilterFields.Uri -> "uri"
    FilterFields.TransportRelation -> "transportportal"
}

internal const val MISSING_VALUE_AGGREGATE = "null"

/**
 * Resources harvested before dcatProfiles was introduced have no value for it, and resources that
 * are not described in accordance with a specialized profile need not state the default one, so a
 * missing value counts as DCAT-AP-NO both when aggregating and when filtering.
 */
internal const val DEFAULT_DCAT_PROFILE = "DCAT_AP_NO"

private const val MAX_AGGREGATION_BUCKETS = 15_000

/**
 * Registers a facet aggregation for [field]: an outer filter aggregation that applies every
 * facet selection except [field]'s own, with a nested terms aggregation that produces the buckets.
 * Documents without a value for the field are counted in the [missingValue] bucket when one is
 * given, and left out otherwise.
 */
internal fun NativeQueryBuilder.addFacetAggregation(
    field: FilterFields,
    facets: Map<FilterFields, FacetFilterClause>,
    missingValue: String? = null,
): NativeQueryBuilder {
    withAggregation(
        field.aggregationName(),
        Aggregation.of { aggregationBuilder ->
            aggregationBuilder
                .filter(facets.excluding(field).toBoolQuery())
                .aggregations(field.aggregationName()) { termsAggregationBuilder ->
                    termsAggregationBuilder.terms { termsBuilder ->
                        val sized = termsBuilder.field(field.jsonPath()).size(MAX_AGGREGATION_BUCKETS)
                        if (missingValue != null) sized.missing(missingValue) else sized
                    }
                }
        },
    )
    return this
}

internal fun termFilter(field: FilterFields, value: String): DSLQuery = DSLQuery.of { queryBuilder ->
    queryBuilder.term { termBuilder ->
        termBuilder.field(field.jsonPath()).value(FieldValue.of(value))
    }
}

internal fun termFilter(field: FilterFields, value: Boolean): DSLQuery = DSLQuery.of { queryBuilder ->
    queryBuilder.term { termBuilder ->
        termBuilder.field(field.jsonPath()).value(FieldValue.of(value))
    }
}

internal fun termsFilter(field: FilterFields, values: List<String>): DSLQuery = DSLQuery.of { queryBuilder ->
    queryBuilder.terms { termsBuilder ->
        termsBuilder
            .field(field.jsonPath())
            .terms { fieldBuilder -> fieldBuilder.value(values.map { FieldValue.of(it) }) }
    }
}

/**
 * Combines [filters] with OR so that a document matches if any of them matches.
 * Used for multiple selections within the same filter type.
 */
internal fun anyOfFilters(filters: List<DSLQuery>): DSLQuery = DSLQuery.of { queryBuilder ->
    queryBuilder.bool { boolBuilder ->
        boolBuilder.should(filters).minimumShouldMatch("1")
    }
}

internal fun matchFilter(field: FilterFields, value: String): DSLQuery = DSLQuery.of { queryBuilder ->
    queryBuilder.match { matchBuilder ->
        matchBuilder.field(field.jsonPath()).query(FieldValue.of(value))
    }
}

internal fun rangeFromDaysAgoFilter(field: FilterFields, daysAgo: Int): DSLQuery = DSLQuery.of { queryBuilder ->
    queryBuilder.range { rangeBuilder ->
        rangeBuilder.term { termRangeBuilder ->
            termRangeBuilder.field(field.jsonPath()).gte("now-${daysAgo}d/d")
        }
    }
}

internal fun existsFilter(field: FilterFields): DSLQuery = DSLQuery.of { queryBuilder ->
    queryBuilder.exists { existsBuilder -> existsBuilder.field(field.jsonPath()) }
}

/** Documents where [field] is absent — used when the client selects the aggregation missing bucket. */
internal fun missingFieldFilter(field: FilterFields): DSLQuery = DSLQuery.of { queryBuilder ->
    queryBuilder.bool { boolBuilder ->
        boolBuilder.mustNot(listOf(existsFilter(field)))
    }
}

/**
 * Builds a facet clause from a list that may include [MISSING_VALUE_AGGREGATE] (`"null"`) to mean
 * "field is missing", OR'd with any concrete values.
 *
 * A whole-filter `value: null` is handled by callers as an alias for missing-only.
 */
internal fun valuesOrMissingClause(field: FilterFields, values: List<String>): FacetFilterClause? {
    val includeMissing = values.contains(MISSING_VALUE_AGGREGATE)
    val concreteValues = values.filter { it != MISSING_VALUE_AGGREGATE }

    return when {
        includeMissing && concreteValues.isEmpty() ->
            FacetFilterClause(mustNots = listOf(existsFilter(field)))

        !includeMissing && concreteValues.isNotEmpty() ->
            FacetFilterClause(filters = listOf(termsFilter(field, concreteValues)))

        includeMissing && concreteValues.isNotEmpty() ->
            FacetFilterClause(
                filters = listOf(
                    anyOfFilters(
                        listOf(
                            termsFilter(field, concreteValues),
                            missingFieldFilter(field),
                        ),
                    ),
                ),
            )

        else -> null
    }
}

internal fun transportProfileFilter(): DSLQuery = termFilter(FilterFields.TransportRelation, true)

/**
 * Filters on a single dcat profile, asking for [DEFAULT_DCAT_PROFILE] also
 * matches datasets that have no profiles at all.
 */
internal fun dcatProfileFilter(value: String): DSLQuery = when (value) {
    DEFAULT_DCAT_PROFILE ->
        DSLQuery.of { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder
                    .should(
                        listOf(
                            termFilter(FilterFields.DcatProfiles, value),
                            missingDcatProfilesFilter(),
                        ),
                    ).minimumShouldMatch("1")
            }
        }

    else -> termFilter(FilterFields.DcatProfiles, value)
}

private fun missingDcatProfilesFilter(): DSLQuery = DSLQuery.of { queryBuilder ->
    queryBuilder.bool { boolBuilder ->
        boolBuilder
            .filter(listOf(termFilter(FilterFields.SearchType, SearchType.DATASET.name)))
            .mustNot(listOf(existsFilter(FilterFields.DcatProfiles)))
    }
}

/**
 * The filters every search and suggestion query needs regardless of caller-specific criteria:
 * exclude deleted documents, restrict to the requested [searchTypes], and apply profile-specific
 * restrictions such as the transport profile.
 */
internal fun commonQueryFilters(searchTypes: List<SearchType>?, profile: SearchProfile?): MutableList<DSLQuery> {
    val queryFilters = mutableListOf(termFilter(FilterFields.Deleted, false))

    searchTypes?.let { queryFilters.add(termsFilter(FilterFields.SearchType, it.map(SearchType::name))) }
    if (profile == SearchProfile.TRANSPORT) queryFilters.add(transportProfileFilter())

    return queryFilters
}

/**
 * A single facet's contribution to the query. [filters] go in bool.filter; [mustNots] go in
 * bool.mustNot (used when a facet asks for the absence of a field).
 */
internal data class FacetFilterClause(val filters: List<DSLQuery> = emptyList(), val mustNots: List<DSLQuery> = emptyList()) {
    fun isEmpty(): Boolean = filters.isEmpty() && mustNots.isEmpty()

    fun toBoolQuery(): DSLQuery = DSLQuery.of { queryBuilder ->
        queryBuilder.bool { boolBuilder ->
            if (filters.isNotEmpty()) boolBuilder.filter(filters)
            if (mustNots.isNotEmpty()) boolBuilder.mustNot(mustNots)
            boolBuilder
        }
    }
}

internal fun Map<FilterFields, FacetFilterClause>.excluding(field: FilterFields): FacetFilterClause {
    val others = filterKeys { it != field }.values
    return FacetFilterClause(
        filters = others.flatMap { it.filters },
        mustNots = others.flatMap { it.mustNots },
    )
}

internal fun Map<FilterFields, FacetFilterClause>.combined(): FacetFilterClause = FacetFilterClause(
    filters = values.flatMap { it.filters },
    mustNots = values.flatMap { it.mustNots },
)

/**
 * Filters that always apply to both hits and aggregations: common constraints plus non-faceted
 * search filters (uri, relations, date ranges).
 */
internal fun alwaysOnFilters(filters: SearchFilters?, searchTypes: List<SearchType>?, profile: SearchProfile?): MutableList<DSLQuery> {
    val queryFilters = commonQueryFilters(searchTypes, profile)

    filters?.relations?.value?.let { queryFilters.add(termFilter(FilterFields.Relations, it)) }
    filters?.lastXDays?.value?.let { queryFilters.add(rangeFromDaysAgoFilter(FilterFields.FirstHarvested, it)) }
    filters?.lastXDaysModified?.value?.let { queryFilters.add(rangeFromDaysAgoFilter(FilterFields.Modified, it)) }
    filters?.uri?.value?.takeIf { it.isNotEmpty() }?.let {
        queryFilters.add(termsFilter(FilterFields.Uri, it))
    }

    return queryFilters
}

/**
 * Faceted filter selections keyed by field. Used so each aggregation can later exclude its own
 * facet while still applying the others.
 */
internal fun facetFilterClauses(filters: SearchFilters?): Map<FilterFields, FacetFilterClause> {
    if (filters == null) return emptyMap()

    val clauses = linkedMapOf<FilterFields, FacetFilterClause>()

    filters.openData?.value?.let {
        clauses[FilterFields.OpenData] = FacetFilterClause(filters = listOf(termFilter(FilterFields.OpenData, it)))
    }

    filters.accessRights?.let { accessRights ->
        when (val value = accessRights.value) {
            null -> clauses[FilterFields.AccessRights] =
                FacetFilterClause(mustNots = listOf(existsFilter(FilterFields.AccessRights)))

            else -> valuesOrMissingClause(FilterFields.AccessRights, value)?.let {
                clauses[FilterFields.AccessRights] = it
            }
        }
    }

    filters.dataTheme?.value?.takeIf { it.isNotEmpty() }?.let {
        clauses[FilterFields.DataTheme] = FacetFilterClause(filters = listOf(termsFilter(FilterFields.DataTheme, it)))
    }

    filters.dcatProfiles?.value?.takeIf { it.isNotEmpty() }?.let { values ->
        clauses[FilterFields.DcatProfiles] =
            FacetFilterClause(filters = listOf(anyOfFilters(values.map { dcatProfileFilter(it) })))
    }

    filters.provenance?.value?.takeIf { it.isNotEmpty() }?.let {
        clauses[FilterFields.Provenance] = FacetFilterClause(filters = listOf(termsFilter(FilterFields.Provenance, it)))
    }

    filters.spatial?.value?.takeIf { it.isNotEmpty() }?.let {
        clauses[FilterFields.Spatial] = FacetFilterClause(filters = listOf(termsFilter(FilterFields.Spatial, it)))
    }

    filters.losTheme?.value?.takeIf { it.isNotEmpty() }?.let {
        clauses[FilterFields.LosTheme] = FacetFilterClause(filters = listOf(termsFilter(FilterFields.LosTheme, it)))
    }

    filters.orgPath?.let { orgPath ->
        when (val value = orgPath.value) {
            null -> clauses[FilterFields.OrgPath] =
                FacetFilterClause(mustNots = listOf(existsFilter(FilterFields.OrgPath)))

            else -> valuesOrMissingClause(FilterFields.OrgPath, value)?.let {
                clauses[FilterFields.OrgPath] = it
            }
        }
    }

    filters.formats?.value?.takeIf { it.isNotEmpty() }?.let { values ->
        clauses[FilterFields.Format] =
            FacetFilterClause(filters = listOf(anyOfFilters(values.map { matchFilter(FilterFields.Format, it) })))
    }

    return clauses
}
