package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.SearchFilter
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.model.SearchResult
import no.digdir.fdk.searchservice.utils.ApiTestContext
import no.digdir.fdk.searchservice.utils.createEmptySearchFilters
import no.digdir.fdk.searchservice.utils.requestApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.test.context.ContextConfiguration


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class Aggregations: ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val SEARCH_FILTER = createEmptySearchFilters()
    private val PATH = "/search"

    @Test
    fun `any search contains all aggregations`() {
        val searchBody = mapper.writeValueAsString(SearchOperation("random search"))
        val response = requestApi(PATH, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(8, result.aggregations.size)
        Assertions.assertTrue(result.aggregations.keys.containsAll(listOf(
            "accessRights", "dataTheme", "format", "losTheme",
            "openData", "orgPath", "provenance", "spatial"
        )))
    }

    @Test
    fun `filter openData = true only aggregates resources with openData = true`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(openData = SearchFilter(true))))
        val response = requestApi(PATH, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(1, result.aggregations["openData"]?.size)
        Assertions.assertEquals("true", result.aggregations["openData"]?.first()?.key)
    }

    @Test
    fun `filter accessRights = PUBLIC only aggregates resources with accessRights = PUBLIC`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(accessRights = SearchFilter("PUBLIC"))))
        val response = requestApi(PATH, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(1, result.aggregations["accessRights"]?.size)
        Assertions.assertEquals("PUBLIC", result.aggregations["accessRights"]?.first()?.key)
    }

    @Test
    fun `aggregations can be larger than the default size 10`() {
        val searchBody = mapper.writeValueAsString(SearchOperation())
        val response = requestApi(PATH, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(10 < (result.aggregations["orgPath"]?.size ?: 0))
    }

    @Test
    fun `objects missing accessRights are aggregated as null`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(accessRights = SearchFilter(null))))
        val response = requestApi(PATH, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(1, result.aggregations["accessRights"]?.size)
        Assertions.assertEquals("null", result.aggregations["accessRights"]?.first()?.key)
    }

    @Test
    fun `objects missing orgPath are aggregated as null`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(orgPath = SearchFilter(null))))
        val response = requestApi(PATH, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(1, result.aggregations["orgPath"]?.size)
        Assertions.assertEquals("null", result.aggregations["orgPath"]?.first()?.key)
    }

}
