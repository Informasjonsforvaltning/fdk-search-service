package no.digdir.fdk.searchservice.service

import co.elastic.clients.elasticsearch._types.FieldValue
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.Suggestion
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Service
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.Query
import co.elastic.clients.elasticsearch._types.query_dsl.Query as DSLQuery

@Service
class SuggestionService(
    private val elasticsearchOperations: ElasticsearchOperations
) {
    private fun suggestResource(query: String, searchType: SearchType?): SearchHits<SearchObject> =
        elasticsearchOperations.search(suggestionQuery(query, searchType), SearchObject::class.java)

    fun suggestResources(query: String, searchType: SearchType?): List<Suggestion> =
        suggestResource(query, searchType )
            .map { it.content }
            .map { it.toSuggestion() }
            .toList()

    private fun SearchObject.toSuggestion(): Suggestion =
        Suggestion(
            id = id,
            title = title,
            uri = uri,
            searchType = searchType
        )

    private fun suggestionQuery(query: String, searchType: SearchType?): Query {
        val builder = NativeQuery.builder()

        if (searchType != null) {
            builder.withFilter { queryBuilder ->
                queryBuilder.bool { boolBuilder ->
                    boolBuilder.must(searchTypeFilter(searchType))
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

    private fun searchTypeFilter(searchType: SearchType): List<DSLQuery> =
        listOf(DSLQuery.of { queryBuilder ->
            queryBuilder.term { termBuilder ->
                termBuilder
                    .field("searchType.keyword")
                    .value(FieldValue.of(searchType.name))
            }
        })
}
