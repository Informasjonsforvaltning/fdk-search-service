package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.FieldValue
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.Suggestion
import no.digdir.fdk.searchservice.model.SuggestionsResult
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Service
import co.elastic.clients.elasticsearch._types.query_dsl.Query as DSLQuery

@Service
class SuggestionService(
    private val elasticsearchOperations: ElasticsearchOperations
) {
    private fun suggestResource(query: String, searchType: List<SearchType>?): SearchHits<SearchObject> =
        elasticsearchOperations.search(suggestionQuery(query, searchType), SearchObject::class.java)

    fun suggestResources(query: String, searchType: List<SearchType>?): SuggestionsResult =
            SuggestionsResult(suggestResource(query, searchType)
                .map { it.content }
                .map { it.toSuggestion() }
                .toList())

    private fun SearchObject.toSuggestion(): Suggestion =
        Suggestion(
            id = id,
            title = title,
            uri = uri,
            searchType = searchType
        )

    private fun suggestionQuery(query: String, searchTypes: List<SearchType>?): Query {
        val builder = NativeQuery.builder()

        if (!searchTypes.isNullOrEmpty()) {
            builder.withFilter { queryBuilder ->
                queryBuilder.bool { boolBuilder ->
                    boolBuilder.should(searchTypeFilter(searchTypes))
                }
            }
        }

        builder.withQuery { queryBuilder ->
            queryBuilder.matchPhrasePrefix { matchBuilder ->
                matchBuilder.query(query)
                    .field("title.nb")
            }
        }

        return builder.build()
    }

    private fun searchTypeFilter(searchTypes: List<SearchType>): List<DSLQuery> {
        return searchTypes.map { searchType ->
            DSLQuery.of { queryBuilder ->
                queryBuilder.term { termBuilder ->
                    termBuilder
                        .field("searchType.keyword")
                        .value(FieldValue.of(searchType.name))
                }
            }
        }
    }
}
