package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.Dataset
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchFilters
import no.digdir.fdk.searchservice.model.SearchOperation
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
class FilterTest: ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val SEARCH_FILTER = SearchFilters(null, null, null, null)
    private val DATASETS_PATH = "/search/datasets"

    @Nested
    inner class IsOpen {
        @Test
        fun `filter datasets on isOpen = true`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(opendata = true)))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            for (dataset in result) {
                Assertions.assertTrue(dataset.isOpenData ?: false)
            }
        }

        @Test
        fun `filter datasets on isOpen = false`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(opendata = false)))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            for (dataset in result) {
                Assertions.assertTrue(dataset.isOpenData == false)
            }
        }
    }

    @Nested
    inner class AccessRight {
        @Test
        fun `filter datasets on accessRight = 'PUBLIC'`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(accessRights = "PUBLIC")))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            for (dataset in result) {
                Assertions.assertTrue(dataset.accessRights?.code?.contains("PUBLIC") ?: false)
            }
        }

        @Test
        fun `filter datasets on non valid accessRight returns empty list`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(accessRights = "")))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.size)
        }
    }

    @Nested
    inner class DataTheme {
        @Test
        fun `filter datasets on one theme, theme = 'REGI'`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(theme = "REGI")))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertTrue(result.size > 0)

            val validValues = listOf("REGI")

            val allThemesValid = result.all { dataset ->
                val themeCodes = dataset.dataTheme?.map { it.code }
                val datasetValid = themeCodes?.containsAll(validValues) ?: false
                datasetValid
            }

            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on multiple themes, theme = 'ENVI,REGI'`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(theme = "ENVI,REGI")))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            val validValues = listOf("ENVI", "REGI")

            val allThemesValid = result.all { dataset ->
                val themeCodes = dataset.dataTheme?.map { it.code }
                val datasetValid = themeCodes?.containsAll(validValues) ?: false
                datasetValid
            }

            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on non-existing theme = '1234' should return nothing`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(theme = "1234")))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.size)
        }
    }

    @Nested
    inner class Provenance {
        @Test
        fun `filter datasets on provenance = 'BRUKER'`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(provenance = "BRUKER")))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<Dataset> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            for (dataset in result) {
                Assertions.assertEquals("BRUKER", dataset.provenance?.code)
            }
        }

        @Test
        fun `filter datasets on non valid provenance = '1234' should return nothing`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(provenance = "1234")))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<Dataset> = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.size)
        }
    }
}
