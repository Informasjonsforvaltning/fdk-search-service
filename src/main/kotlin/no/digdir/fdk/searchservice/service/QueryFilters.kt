package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders
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
 * Registers a terms aggregation for [field], collapsing the boilerplate that would otherwise be
 * repeated for every facet shown in the search response. Documents without a value for the field
 * are counted in the [missingValue] bucket when one is given, and left out otherwise.
 */
internal fun NativeQueryBuilder.addTermsAggregation(field: FilterFields, missingValue: String? = null): NativeQueryBuilder {
    withAggregation(
        field.aggregationName(),
        AggregationBuilders.terms { builder ->
            val sized = builder.field(field.jsonPath()).size(MAX_AGGREGATION_BUCKETS)
            if (missingValue != null) sized.missing(missingValue) else sized
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

internal fun transportProfileFilter(): DSLQuery = termFilter(FilterFields.TransportRelation, true)

/**
 * Filters on any of the given dcat profiles, a resource matching one of them is a hit. Asking for
 * [DEFAULT_DCAT_PROFILE] also matches datasets that have no profiles at all.
 */
internal fun dcatProfilesFilter(values: List<String>): DSLQuery = when {
    values.contains(DEFAULT_DCAT_PROFILE) ->
        DSLQuery.of { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder
                    .should(
                        listOf(
                            termsFilter(FilterFields.DcatProfiles, values),
                            missingDcatProfilesFilter(),
                        ),
                    ).minimumShouldMatch("1")
            }
        }

    else -> termsFilter(FilterFields.DcatProfiles, values)
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
