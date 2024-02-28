package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.SearchFilters
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.utils.ApiTestContext
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
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class DataServiceSearchTest : ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val searchFilters = SearchFilters(
        null, null, null,
        null, null, null, null, null
    )
    private val SEARCH_QUERY = "test"
    private val SEARCH_QUERY_NO_HITS = "nohits"
    private val SEARCH_QUERYS_HIT_ALL_SEARCH_FIELDS =
        listOf(
            "title",
            "description",
            "keyword",
        )
    private val DATA_SERVICES_PATH = "/search/dataservices"

    @Test
    fun `search with at least one hit`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY, searchFilters))
        val response = requestApi(DATA_SERVICES_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(result.size, 0)
    }

    @Test
    fun `check searchType`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY, searchFilters))
        val response = requestApi(DATA_SERVICES_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        result.forEach {
            Assertions.assertTrue(it.searchType == SearchType.DATA_SERVICE)
        }
    }

    @Test
    fun `search with no hits`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY_NO_HITS, searchFilters))
        val response = requestApi(DATA_SERVICES_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(result.size, 0)
    }

    @Test
    fun `search with empty query`() {
        val searchBody = mapper.writeValueAsString(SearchOperation("", searchFilters))
        val response = requestApi(DATA_SERVICES_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(result.isNotEmpty())
    }

    @Test
    fun `search and hit all search fields successfully`() {
        SEARCH_QUERYS_HIT_ALL_SEARCH_FIELDS.forEach {
            val searchBody = mapper.writeValueAsString(SearchOperation(it, searchFilters))
            val response = requestApi(DATA_SERVICES_PATH, port, searchBody, POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            if (result.isEmpty()) Assertions.fail<String>("No hit for query: $it")
        }
    }
}
