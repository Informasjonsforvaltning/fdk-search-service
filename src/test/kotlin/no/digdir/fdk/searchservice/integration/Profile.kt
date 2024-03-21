package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.SearchFilter
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.model.SearchProfile
import no.digdir.fdk.searchservice.model.SearchResult
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.utils.ApiTestContext
import no.digdir.fdk.searchservice.utils.createEmptySearchFilters
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
class Profile: ApiTestContext() {
    private val PATH = "/search"
    private val mapper = jacksonObjectMapper()

    @Nested
    inner class Transport {
        private val transportProfileLosPaths = listOf(
            "trafikk-og-transport/mobilitetstilbud",
            "trafikk-og-transport/trafikkinformasjon",
            "trafikk-og-transport/veg-og-vegregulering",
            "trafikk-og-transport/yrkestransport")

        @Test
        fun `transport filters are added when no other filters are present`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(profile = SearchProfile.TRANSPORT))
            val response = requestApi(PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            val resultLosPaths: Set<String> = result.hits
                .flatMap { it.losTheme ?: emptySet() }
                .flatMap { it.losPaths ?: emptySet() }
                .toSet()

            Assertions.assertNotEquals(0, result.hits.size)
            Assertions.assertTrue(transportProfileLosPaths.any { resultLosPaths.contains(it) })
        }
        @Test
        fun `transport filters are added on endpoint for specific resource`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(profile = SearchProfile.TRANSPORT))
            val response = requestApi("$PATH/datasets", port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            val resultLosPaths: Set<String> = result.hits
                .flatMap { it.losTheme ?: emptySet() }
                .flatMap { it.losPaths ?: emptySet() }
                .toSet()

            Assertions.assertNotEquals(0, result.hits.size)
            Assertions.assertTrue(transportProfileLosPaths.any { resultLosPaths.contains(it) })
            result.hits.forEach { Assertions.assertTrue(it.searchType == SearchType.DATASET) }
        }

        @Test
        fun `transport filters are added in combination with los filter`() {
            val filters = createEmptySearchFilters().copy(losTheme = SearchFilter(listOf("test")))
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = filters, profile = SearchProfile.TRANSPORT))
            val response = requestApi(PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            val resultLosPaths: Set<String> = result.hits
                .flatMap { it.losTheme ?: emptySet() }
                .flatMap { it.losPaths ?: emptySet() }
                .toSet()

            Assertions.assertNotEquals(0, result.hits.size)
            Assertions.assertTrue(transportProfileLosPaths.any { resultLosPaths.contains(it) })
            Assertions.assertTrue(resultLosPaths.contains("test"))
        }
    }

}
