package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.SearchFilters
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchOperation
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
class SearchObjectTest: ApiTestContext() {
    private val PATH = "/search"
    private val PATH_NONE_EXISTING_RESOURCE = "$PATH/none-existing-resource"
    private val SEARCH_QUERY = "test"
    private val SEARCH_QUERY_NO_HITS = "nohits"
    private val SEARCH_QUERYS_SUCCESS_ALL_TEXT_FIELDS =
        listOf("uri",
            "accessRights",
            "catalog",
            "dataTheme",
            "description",
            "fdkFormatPrefixed",
            "harvest",
            "keyword",
            "losTheme",
            "organization",
            "provenance",
            "spatial",
            "title")
    private val mapper = jacksonObjectMapper()
    private val searchFilters = SearchFilters(null, null, null,
        null, null, null, null, null)

    @Test
    fun `search with at least one hit`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY, searchFilters))
        val response = requestApi(PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertTrue (result.size > 0)
    }

    @Test
    fun `search with no hits`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY_NO_HITS, searchFilters))
        val response = requestApi(PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(result.size, 0)
    }

    @Test
    fun `search with empty query`() {
        val searchBody = mapper.writeValueAsString(SearchOperation("", searchFilters))
        val response = requestApi(PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(result.size, 0)
    }

    @Test
    fun `search and hit all text fields successfully`() {
        SEARCH_QUERYS_SUCCESS_ALL_TEXT_FIELDS.forEach {
            val searchBody = mapper.writeValueAsString(SearchOperation(it, searchFilters))
            val response = requestApi(PATH, port, searchBody, POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            if (result.isEmpty()) Assertions.fail<String>("No hit for query: $it")
        }
    }

    @Test
    fun `not found for unsupported searchTypes`() {
        val searchBody = mapper.writeValueAsString(SearchOperation("", searchFilters))
        val response = requestApi(PATH_NONE_EXISTING_RESOURCE, port, searchBody, POST)
        Assertions.assertEquals(404, response["status"])
    }
}
