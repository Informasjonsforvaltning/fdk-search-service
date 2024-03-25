package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.SuggestionsResult
import no.digdir.fdk.searchservice.utils.ApiTestContext
import no.digdir.fdk.searchservice.utils.requestApi
import org.junit.jupiter.api.*
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
class SuggestionTest : ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val SUGGESTIONS_PATH = "/suggestions"
    private val GET = HttpMethod.GET

    @Test
    fun `get suggestions for query 'title'`() {
        val response = requestApi("$SUGGESTIONS_PATH?q=title", port, null, GET)
        Assertions.assertEquals(200, response["status"])

        val result: SuggestionsResult = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(0, result.suggestions.size)

        Assertions.assertTrue(result.suggestions.all {
            it.title?.nb?.contains("title") == true
        })
        Assertions.assertTrue(result.suggestions.any {
            it.description?.nb?.contains("Test description") ?: false &&
                it.organization?.id?.contains("Test publisher") ?: false
        })
    }

    @Test
    fun `get no suggestions for non-existing query`() {
        val response = requestApi("$SUGGESTIONS_PATH?q=nonExistingQuery", port, null, GET)
        Assertions.assertEquals(200, response["status"])

        val result: SuggestionsResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(0, result.suggestions.size)
    }

    @Test
    fun `get suggestions for services and events`() {
        val response = requestApi(
            "$SUGGESTIONS_PATH/public-services-and-events?q=title",
            port, null, GET
        )
        Assertions.assertEquals(200, response["status"])

        val result: SuggestionsResult = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(result.suggestions.size > 1)

        val validResult = result.suggestions.all { resource ->
            resource.title?.nb?.contains("title") == true &&
                (resource.searchType == SearchType.SERVICE || resource.searchType == SearchType.EVENT)
        }
        Assertions.assertTrue(validResult)
    }

    @Nested
    inner class SearchTypeFilter {
        @Test
        fun `get suggestion for datasets`() {
            val response = requestApi("$SUGGESTIONS_PATH/datasets?q=title", port, null, GET)
            Assertions.assertEquals(200, response["status"])

            val result: SuggestionsResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.suggestions.size)

            val validResult = result.suggestions.all { resource ->
                resource.title?.nb?.contains("title") == true && resource.searchType == SearchType.DATASET
            }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `get suggestions for concepts`() {
            val response = requestApi("$SUGGESTIONS_PATH/concepts?q=title", port, null, GET)
            Assertions.assertEquals(200, response["status"])

            val result: SuggestionsResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.suggestions.size)

            val validResult = result.suggestions.all { resource ->
                resource.title?.nb?.contains("title") == true && resource.searchType == SearchType.CONCEPT
            }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `get suggestions for data service`() {
            val response = requestApi("$SUGGESTIONS_PATH/dataservices?q=title", port, null, GET)
            Assertions.assertEquals(200, response["status"])

            val result: SuggestionsResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.suggestions.size)

            val validResult = result.suggestions.all { resource ->
                resource.title?.nb?.contains("title") == true && resource.searchType == SearchType.DATA_SERVICE
            }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `get suggestion with transport profile`() {
            val response = requestApi("$SUGGESTIONS_PATH/datasets?q=title&profile=TRANSPORT", port, null, GET)
            Assertions.assertEquals(200, response["status"])

            val result: SuggestionsResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(3, result.suggestions.size)

            val validResult = result.suggestions.all { resource ->
                resource.title?.nb?.contains("NB Test title") == true && resource.searchType == SearchType.DATASET
            }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `get suggestion with org id`() {
            val response = requestApi("$SUGGESTIONS_PATH/concepts?q=title&org=102117858", port, null, GET)
            Assertions.assertEquals(200, response["status"])

            val result: SuggestionsResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(1, result.suggestions.size)

            val validResult = result.suggestions.all { resource ->
                resource.title?.nb?.contains("NB Test prefLabel, title") == true && resource.searchType == SearchType.CONCEPT
            }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `non valid resource type should return not found`() {
            val response = requestApi("$SUGGESTIONS_PATH/nonvalid?q=title", port, null, GET)
            Assertions.assertEquals(404, response["status"])
        }
    }
}
