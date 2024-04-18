package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.FieldValue
import io.micrometer.core.instrument.Metrics
import no.digdir.fdk.searchservice.model.*
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Service
import kotlin.time.toJavaDuration
import co.elastic.clients.elasticsearch._types.query_dsl.Query as DSLQuery

@Service
class SuggestionService(
    private val elasticsearchOperations: ElasticsearchOperations
) {
    private fun suggestResource(
        query: String,
        searchType: List<SearchType>?,
        profile: SearchProfile?,
        orgId: String?
    ): SearchHits<SearchObject> =
        elasticsearchOperations.search(suggestionQuery(query, searchType, profile, orgId), SearchObject::class.java)

    fun suggestResources(
        query: String,
        searchType: List<SearchType>?,
        profile: SearchProfile?,
        orgId: String?
    ): SuggestionsResult {
        val (result, timeElapsed) = kotlin.time.measureTimedValue {
            SuggestionsResult(suggestResource(query, searchType, profile, orgId)
                .map { it.content }
                .map { it.toSuggestion() }
                .toList())
        }
        Metrics.timer("search_suggestion").record(timeElapsed.toJavaDuration())
        return result
    }

    private fun SearchObject.toSuggestion(): Suggestion =
        Suggestion(
            id = id,
            title = title,
            description = description,
            uri = uri,
            organization = organization,
            searchType = searchType
        )

    private fun suggestionQuery(
        query: String,
        searchTypes: List<SearchType>?,
        profile: SearchProfile?,
        orgId: String?
    ): Query {
        val builder = NativeQuery.builder()

        builder.withQuery { queryBuilder ->
            queryBuilder.bool { boolBuilder ->
                boolBuilder.should {
                    it.matchPhrasePrefix { matchBuilder ->
                        matchBuilder
                            .field("title.nb")
                            .query(query)
                    }

                }

                boolBuilder.should {
                    it.matchPhrase { matchBuilder ->
                        matchBuilder
                            .field("title.nb")
                            .query(query)
                    }
                }
                boolBuilder.minimumShouldMatch("1")
                boolBuilder.filter(createQueryFilters(searchTypes, profile, orgId))
            }
        }

        return builder.build()
    }

    private fun createQueryFilters(
        searchTypes: List<SearchType>?,
        profile: SearchProfile?,
        orgId: String?
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

        if (orgId != null) {
            queryFilters.add(
                DSLQuery.of { queryBuilder ->
                    queryBuilder.term { termBuilder ->
                        termBuilder
                            .field(FilterFields.OrgId.jsonPath())
                            .value(FieldValue.of(orgId))
                    }
                })
        }

        if (profile == SearchProfile.TRANSPORT) queryFilters.add(filtersForProfile(profile))

        return queryFilters
    }
}
