package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.SuggestionsResult
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
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class SuggestionTest : ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val suggestionsPath = "/suggestions"
    private val httpGet = HttpMethod.GET

    @Test
    fun `httpGet suggestions for query 'title'`() {
        val response = requestApi("$suggestionsPath?q=title", port, null, httpGet)
        Assertions.assertEquals(200, response["status"])

        val result: SuggestionsResult = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(0, result.suggestions.size)

        Assertions.assertTrue(
            result.suggestions.all {
                it.title?.nb?.contains("title") == true
            },
        )
        Assertions.assertTrue(
            result.suggestions.any {
                it.description?.nb?.contains("Test description") ?: false &&
                    it.organization?.id?.contains("Test publisher") ?: false
            },
        )
    }

    @Test
    fun `httpGet no suggestions for non-existing query`() {
        val response = requestApi("$suggestionsPath?q=nonExistingQuery", port, null, httpGet)
        Assertions.assertEquals(200, response["status"])

        val result: SuggestionsResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(0, result.suggestions.size)
    }

    @Test
    fun `httpGet suggestions for services and events`() {
        val response =
            requestApi(
                "$suggestionsPath/public-services-and-events?q=title",
                port,
                null,
                httpGet,
            )
        Assertions.assertEquals(200, response["status"])

        val result: SuggestionsResult = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(result.suggestions.size > 1)

        val validResult =
            result.suggestions.all { resource ->
                resource.title?.nb?.contains("title") == true &&
                    (resource.searchType == SearchType.SERVICE || resource.searchType == SearchType.EVENT)
            }
        Assertions.assertTrue(validResult)
    }

    @Nested
    inner class SearchTypeFilter {
        @Test
        fun `httpGet suggestion for datasets`() {
            val response = requestApi("$suggestionsPath/datasets?q=title", port, null, httpGet)
            Assertions.assertEquals(200, response["status"])

            val result: SuggestionsResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.suggestions.size)

            val validResult =
                result.suggestions.all { resource ->
                    resource.title?.nb?.contains("title") == true && resource.searchType == SearchType.DATASET
                }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `httpGet suggestions for concepts`() {
            val response = requestApi("$suggestionsPath/concepts?q=title", port, null, httpGet)
            Assertions.assertEquals(200, response["status"])

            val result: SuggestionsResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.suggestions.size)

            val validResult =
                result.suggestions.all { resource ->
                    resource.title?.nb?.contains("title") == true && resource.searchType == SearchType.CONCEPT
                }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `httpGet suggestions for data service`() {
            val response = requestApi("$suggestionsPath/dataservices?q=title", port, null, httpGet)
            Assertions.assertEquals(200, response["status"])

            val result: SuggestionsResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.suggestions.size)

            val validResult =
                result.suggestions.all { resource ->
                    resource.title?.nb?.contains("title") == true && resource.searchType == SearchType.DATA_SERVICE
                }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `httpGet suggestion with transport profile`() {
            val response = requestApi("$suggestionsPath/datasets?q=title&profile=TRANSPORT", port, null, httpGet)
            Assertions.assertEquals(200, response["status"])

            val result: SuggestionsResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(3, result.suggestions.size)

            val validResult =
                result.suggestions.all { resource ->
                    resource.title?.nb?.contains("NB Test title") == true && resource.searchType == SearchType.DATASET
                }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `httpGet suggestion with org id`() {
            val response = requestApi("$suggestionsPath/concepts?q=title&org=102117858", port, null, httpGet)
            Assertions.assertEquals(200, response["status"])

            val result: SuggestionsResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(1, result.suggestions.size)

            val validResult =
                result.suggestions.all { resource ->
                    resource.title?.nb?.contains("NB Test prefLabel, title") == true && resource.searchType == SearchType.CONCEPT
                }
            Assertions.assertTrue(validResult)
        }

        @Test
        fun `non valid resource type should return not found`() {
            val response = requestApi("$suggestionsPath/nonvalid?q=title", port, null, httpGet)
            Assertions.assertEquals(404, response["status"])
        }
    }
}
