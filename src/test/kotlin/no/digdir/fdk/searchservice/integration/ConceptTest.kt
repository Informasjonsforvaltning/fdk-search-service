package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.model.SearchResult
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.utils.ApiTestContext
import no.digdir.fdk.searchservice.utils.createEmptySearchFilters
import no.digdir.fdk.searchservice.utils.requestApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod.POST
import org.springframework.test.context.ContextConfiguration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class ConceptTest : ApiTestContext() {
    private val conceptsPath = "/search/concepts"
    private val searchQuery = "test"
    private val searchQueryNoHits = "nohits"
    private val searchQueriesHitAllSearchFields =
        listOf("definition", "prefLabel")
    private val searchQueriesHitAdditionalTitles =
        listOf("Frarådet term", "Tillatt term")
    private val searchFilters = createEmptySearchFilters()
    private val mapper = jacksonObjectMapper()

    @Test
    fun `search with at least one hit`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(searchQuery, searchFilters))
        val response = requestApi(conceptsPath, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(result.hits.isNotEmpty())
    }

    @Test
    fun `check searchType`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(searchQuery, searchFilters))
        val response = requestApi(conceptsPath, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        result.hits.forEach {
            Assertions.assertTrue(it.searchType == SearchType.CONCEPT)
        }
    }

    @Test
    fun `search concepts with no hits`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(searchQueryNoHits, searchFilters))
        val response = requestApi(conceptsPath, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(result.hits.size, 0)
    }

    @Test
    fun `search concepts with empty query`() {
        val searchBody = mapper.writeValueAsString(SearchOperation("", searchFilters))
        val response = requestApi(conceptsPath, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(result.hits.isNotEmpty())
    }

    @Test
    fun `search and hit all search fields successfully`() {
        searchQueriesHitAllSearchFields.forEach {
            val searchBody = mapper.writeValueAsString(SearchOperation(it, searchFilters))
            val response = requestApi(conceptsPath, port, searchBody, POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            if (result.hits.isEmpty()) Assertions.fail<String>("No hit for query: $it")
        }
    }

    @Test
    fun `search and hit additional titles`() {
        searchQueriesHitAdditionalTitles.forEach { searchQuery ->
            val searchBody = mapper.writeValueAsString(SearchOperation(searchQuery, searchFilters))
            val response = requestApi(conceptsPath, port, searchBody, POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            if (result.hits.isEmpty()) Assertions.fail<String>("No hit for query: $searchQuery")

            val hasAdditionalTitlesHits =
                result.hits.any { hit ->
                    hit.additionalTitles?.isNotEmpty() ?: false
                }

            Assertions.assertTrue(hasAdditionalTitlesHits)
        }
    }
}
