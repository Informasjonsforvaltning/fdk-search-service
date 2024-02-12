package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.Dataservice
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
class DataserviceSearchTest: ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val SEARCH_QUERY = "test"
    private val SEARCH_QUERY_NO_HITS = "nohits"
    private val SEARCH_QUERYS_HIT_ALL_FIELDS = listOf("title","description","publisher")
    private val DATASERVICE_PATH = "/dataservices"

    @Test
    fun `search dataservices with at least one hit`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY))
        val response = requestApi(DATASERVICE_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<Dataservice> = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(result.size, 0)
    }

    @Test
    fun `search dataservices with no hits`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY_NO_HITS))
        val response = requestApi(DATASERVICE_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<Dataservice> = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(result.size, 0)
    }

    @Test
    fun `search dataservices with empty query`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(""))
        val response = requestApi(DATASERVICE_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<Dataservice> = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(result.size, 0)
    }

    @Test
    fun `search and hit all fields successfully`() {
        SEARCH_QUERYS_HIT_ALL_FIELDS.forEach {
            val searchBody = mapper.writeValueAsString(SearchOperation(it))
            val response = requestApi(DATASERVICE_PATH, port, searchBody, POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<Dataservice> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(result.size, 0)
        }
    }
}
