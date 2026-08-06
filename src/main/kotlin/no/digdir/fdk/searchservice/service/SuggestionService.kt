package no.digdir.fdk.searchservice.service

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
                listOf("title.nb", "title.nn", "title.no", "title.en").forEach { field ->
                    boolBuilder.should {
                        it.matchPhrasePrefix { matchBuilder ->
                            matchBuilder
                                .field(field)
                                .query(query)
                        }

                    }

                    boolBuilder.should {
                        it.matchPhrase { matchBuilder ->
                            matchBuilder
                                .field(field)
                                .query(query)
                        }
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
        val queryFilters = commonQueryFilters(searchTypes, profile)

        orgId?.let { queryFilters.add(termFilter(FilterFields.OrgId, it)) }

        return queryFilters
    }
}
