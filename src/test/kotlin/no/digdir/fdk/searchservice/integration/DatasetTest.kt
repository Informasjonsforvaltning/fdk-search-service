package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchFilters
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
class SearchDatasetTest: ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val SEARCH_QUERY = "test"
    private val SEARCH_FILTER = SearchFilters(null, null, null)
    private val SEARCH_QUERY_NO_HITS = "nohits"
    private val SEARCH_QUERYS_HIT_ALL_FIELDS =
        listOf("title", "description", "keyword", "theme", "losTheme", "publisher", "accessRights", "spatial",
            "provenance", "harvest", "catalog")
    private val DATASETS_PATH = "/search/datasets"

    @Test
    fun `search datasets with at least one hit`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(result.size > 0)
    }

    @Test
    fun `search datasets with no hits`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY_NO_HITS))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(0, result.size)
    }

    @Test
    fun `search datasets with empty query`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(""))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(0, result.size)
    }

    @Test
    fun `search and hit all datasets fields successfully`() {
        SEARCH_QUERYS_HIT_ALL_FIELDS.forEach {
            val searchBody = mapper.writeValueAsString(SearchOperation(it))
            val response = requestApi(DATASETS_PATH, port, searchBody, POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertTrue(result.size > 0)
        }
    }

    @Test
    fun `filter datasets on isOpen = true`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(opendata = true)))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
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
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(0, result.size)

        for (dataset in result) {
            Assertions.assertTrue(dataset.isOpenData == false)
        }
    }

    @Test
    fun `filter datasets on accessRight = 'PUBLIC'`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(accessRights = "PUBLIC")))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(0, result.size )

        for (dataset in result) {
            Assertions.assertTrue(dataset.accessRights?.code?.contains("PUBLIC") ?: false)
        }
    }

    @Test
    fun `filter datasets on non valid accessRight returns empty list`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(accessRights = "")))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(0, result.size)
    }

    @Test
    fun `filter datasets on one theme, theme = 'REGI'`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(theme = "REGI")))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
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
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(theme = "ENVI,REGI")))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
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
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: List<SearchObject> = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(0, result.size)
    }
}
