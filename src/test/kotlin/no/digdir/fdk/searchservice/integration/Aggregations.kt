package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.SearchFilter
import no.digdir.fdk.searchservice.model.SearchFilters
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
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class Aggregations : ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val searchFilter = createEmptySearchFilters()
    private val path = "/search"
    private val datasetsPath = "/search/datasets"

    private fun search(filters: SearchFilters? = null, path: String = this.path): SearchResult {
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = filters))
        val response = requestApi(path, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])
        return mapper.readValue(response["body"] as String)
    }

    @Test
    fun `any search contains all aggregations`() {
        val searchBody = mapper.writeValueAsString(SearchOperation("random search"))
        val response = requestApi(path, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(9, result.aggregations.size)
        Assertions.assertTrue(
            result.aggregations.keys.containsAll(
                listOf(
                    "accessRights",
                    "dataTheme",
                    "dcatProfiles",
                    "format",
                    "losTheme",
                    "openData",
                    "orgPath",
                    "provenance",
                    "spatial",
                ),
            ),
        )
    }

    @Test
    fun `openData aggregation ignores its own filter while hits remain filtered`() {
        val result = search(searchFilter.copy(openData = SearchFilter(true)))

        Assertions.assertTrue(result.hits.isNotEmpty())
        Assertions.assertTrue(result.hits.all { it.isOpenData == true })

        val openDataKeys = result.aggregations["openData"]?.map { it.key }?.toSet()
        Assertions.assertTrue(openDataKeys?.contains("true") == true)
        Assertions.assertTrue((openDataKeys?.size ?: 0) > 1)
    }

    @Test
    fun `accessRights aggregation ignores its own filter while hits remain filtered`() {
        val result = search(searchFilter.copy(accessRights = SearchFilter(listOf("PUBLIC"))))

        Assertions.assertTrue(result.hits.isNotEmpty())
        Assertions.assertTrue(result.hits.all { it.accessRights?.code?.contains("PUBLIC") == true })

        val accessRightsKeys = result.aggregations["accessRights"]?.map { it.key }?.toSet()
        Assertions.assertTrue(accessRightsKeys?.contains("PUBLIC") == true)
        Assertions.assertTrue((accessRightsKeys?.size ?: 0) > 1)
    }

    @Test
    fun `aggregations can be larger than the default size 10`() {
        val result = search()
        Assertions.assertTrue(10 < (result.aggregations["orgPath"]?.size ?: 0))
    }

    @Test
    fun `datasets without dcatProfiles are aggregated as the default profile`() {
        val result = search(path = datasetsPath)
        val counts = result.aggregations["dcatProfiles"]?.associate { it.key to it.count }

        Assertions.assertEquals(2, counts?.get("DCAT_AP_NO"))
        Assertions.assertEquals(1, counts?.get("HVD_DCAT_AP_NO"))
        Assertions.assertEquals(2, counts?.get("MOBILITY_DCAT_AP"))
    }

    @Test
    fun `accessRights aggregation ignores missing-field filter while hits remain filtered`() {
        val result = search(searchFilter.copy(accessRights = SearchFilter(listOf("null"))))

        Assertions.assertTrue(result.hits.isNotEmpty())
        Assertions.assertTrue(result.hits.all { it.accessRights == null })

        val accessRightsKeys = result.aggregations["accessRights"]?.map { it.key }?.toSet()
        Assertions.assertTrue(accessRightsKeys?.contains("null") == true)
        Assertions.assertTrue((accessRightsKeys?.size ?: 0) > 1)
    }

    @Test
    fun `orgPath aggregation ignores missing-field filter while hits remain filtered`() {
        val result = search(searchFilter.copy(orgPath = SearchFilter(listOf("null"))))

        Assertions.assertTrue(result.hits.isNotEmpty())
        Assertions.assertTrue(result.hits.all { it.organization?.orgPath == null })

        val orgPathKeys = result.aggregations["orgPath"]?.map { it.key }?.toSet()
        Assertions.assertTrue(orgPathKeys?.contains("null") == true)
        Assertions.assertTrue((orgPathKeys?.size ?: 0) > 1)
    }

    @Test
    fun `theme and org aggregations each exclude their own filter`() {
        val result =
            search(
                searchFilter.copy(
                    dataTheme = SearchFilter(listOf("ENVI")),
                    orgPath = SearchFilter(listOf("/FYLKE")),
                ),
                datasetsPath,
            )

        Assertions.assertTrue(result.hits.isNotEmpty())
        Assertions.assertTrue(
            result.hits.all { hit ->
                hit.dataTheme?.any { it.code == "ENVI" } == true &&
                    hit.organization?.orgPath == "/FYLKE"
            },
        )

        val themeKeys = result.aggregations["dataTheme"]?.map { it.key }?.toSet()
        Assertions.assertTrue(themeKeys?.containsAll(listOf("ENVI", "REGI")) == true)

        val orgKeys = result.aggregations["orgPath"]?.map { it.key }?.toSet()
        Assertions.assertTrue(orgKeys?.contains("/FYLKE") == true)
        Assertions.assertTrue(orgKeys?.any { it.startsWith("/STAT") } == true)
    }

    @Test
    fun `openData and accessRights aggregations exclude each other`() {
        val result =
            search(
                searchFilter.copy(
                    openData = SearchFilter(true),
                    accessRights = SearchFilter(listOf("PUBLIC")),
                ),
            )

        Assertions.assertTrue(result.hits.isNotEmpty())
        Assertions.assertTrue(
            result.hits.all { it.isOpenData == true && it.accessRights?.code?.contains("PUBLIC") == true },
        )

        val openDataKeys = result.aggregations["openData"]?.map { it.key }?.toSet()
        Assertions.assertTrue(openDataKeys?.contains("true") == true)
        Assertions.assertTrue((openDataKeys?.size ?: 0) > 1)

        val accessRightsKeys = result.aggregations["accessRights"]?.map { it.key }?.toSet()
        Assertions.assertTrue(accessRightsKeys?.contains("PUBLIC") == true)
        Assertions.assertTrue((accessRightsKeys?.size ?: 0) > 1)
    }
}
