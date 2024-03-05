package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.Pagination
import no.digdir.fdk.searchservice.model.SearchFilters
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.model.SearchResult
import no.digdir.fdk.searchservice.utils.ApiTestContext
import no.digdir.fdk.searchservice.utils.requestApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.test.context.ContextConfiguration
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class Pagination : ApiTestContext() {
    private val SEARCH_PATH = "/search"
    private val mapper = jacksonObjectMapper()
    private val SEARCH_FILTER = SearchFilters(
        null, null, null,
        null, null, null, null, null, null, null
    )

    @Test
    fun `SearchResult has more than one pages`() {
        val searchBody = mapper.writeValueAsString(SearchOperation("", SEARCH_FILTER))
        val response = requestApi(SEARCH_PATH, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(result.page.totalPages > 1)
        Assertions.assertEquals(10, result.hits.size)
    }

    @Test
    fun `get second search page`() {
        val searchBody = mapper.writeValueAsString(SearchOperation("", SEARCH_FILTER, pagination = Pagination(1, 10)))
        val response = requestApi(SEARCH_PATH, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(result.page.totalPages > 0)
    }

    @Test
    fun `change the number of items per page`() {
        val searchBody = mapper.writeValueAsString(SearchOperation("", SEARCH_FILTER, pagination = Pagination(0, 1000)))
        val response = requestApi(SEARCH_PATH, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(result.page.totalPages, 1)
    }
}
