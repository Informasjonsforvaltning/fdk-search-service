package no.digdir.fdk.searchservice.integration
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.*
import no.digdir.fdk.searchservice.utils.ApiTestContext
import no.digdir.fdk.searchservice.utils.requestApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
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
class SuggestionTest: ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val SUGGESTIONS_PATH = "/suggestions"
    private val GET = HttpMethod.GET

    @Test
    fun `get suggestions for query 'title'`() {
        val response = requestApi("$SUGGESTIONS_PATH?q=title", port, null, GET)
        Assertions.assertEquals(200, response["status"])

        val result: List<Suggestion> = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(0, result.size)

        val containsTest = result.stream()
            .allMatch { resource ->
                resource.title?.nb?.contains("title") ?: false
            }
        Assertions.assertTrue(containsTest)
    }

    @Test
    fun `get no suggestions for non-existing query`() {
        val response = requestApi("$SUGGESTIONS_PATH?q=nonExistingQuery", port, null, GET)
        Assertions.assertEquals(200, response["status"])

        val result: List<Suggestion> = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(0, result.size)
    }


    @Nested
    inner class SearchTypeFilter {
        @Test
        fun `get suggestion for datasets`() {
            val response = requestApi("$SUGGESTIONS_PATH/datasets?q=title", port, null, GET)
            Assertions.assertEquals(200, response["status"])

            val result: List<Suggestion> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            val validResult = result.all { resource ->
                resource.title?.nb?.contains("title") == true && resource.searchType == SearchType.DATASET
            }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `get suggestions for concepts`() {
            val response = requestApi("$SUGGESTIONS_PATH/concepts?q=title", port, null, GET)
            Assertions.assertEquals(200, response["status"])

            val result: List<Suggestion> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            val validResult = result.all { resource ->
                resource.title?.nb?.contains("title") == true && resource.searchType == SearchType.CONCEPT
            }
            Assertions.assertTrue(validResult)
        }
    }

}





