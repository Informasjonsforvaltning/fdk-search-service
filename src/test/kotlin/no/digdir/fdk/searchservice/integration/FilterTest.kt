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
class FilterTest: ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val SEARCH_FILTER = SearchFilters(null, null, null,
        null, null, null, null, null)
    private val DATASETS_PATH = "/search/datasets"

    @Nested
    inner class IsOpen {
        @Test
        fun `filter datasets on isOpen = true`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(opendata = SearchFilter(true))))
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
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(opendata = SearchFilter(false))))
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
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(accessRights = SearchFilter("PUBLIC"))))
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
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(accessRights = SearchFilter(""))))
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
            val searchBody = mapper.writeValueAsString(
                SearchOperation(
                    filters = SEARCH_FILTER.copy(
                        theme = SearchFilter(
                            listOf("REGI")
                        )
                    )
                )
            )
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertTrue(result.isNotEmpty())

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
                mapper.writeValueAsString(
                    SearchOperation(
                        filters = SEARCH_FILTER.copy(
                            theme = SearchFilter(
                                listOf(
                                    "ENVI",
                                    "REGI"
                                )
                            )
                        )
                    )
                )
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
            val searchBody = mapper.writeValueAsString(
                SearchOperation(filters = SEARCH_FILTER.copy(theme = SearchFilter(listOf("1234"))))
            )
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
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(provenance = SearchFilter("BRUKER"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            for (dataset in result) {
                Assertions.assertEquals("BRUKER", dataset.provenance?.code)
            }
        }

        @Test
        fun `filter datasets on non valid provenance = '1234' should return nothing`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(provenance = SearchFilter("1234"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.size)
        }
    }

    @Nested
    inner class Spatial {
        @Test
        fun `filter datasets on one spatial, spatial = 'Norge'`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(spatial = SearchFilter("Norge"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            val validValues = listOf("Norge")

            val allThemesValid = result.all { dataset ->
                val spatialCodes = dataset.spatial?.map { it.prefLabel?.nb }
                val datasetValid = spatialCodes?.containsAll(validValues) ?: false
                datasetValid
            }

            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on multiple spatial, spatial = 'Norge,Spania'`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(spatial = SearchFilter("Norge,Spania"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            val validValues = listOf("Norge", "Spania")

            val allThemesValid = result.all { dataset ->
                val spatialCodes = dataset.spatial?.map { it.prefLabel?.nb }
                val datasetValid = spatialCodes?.containsAll(validValues) ?: false
                datasetValid
            }

            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on non-existing spatial = '1234' should return nothing`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(spatial = SearchFilter("1234"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.size)
        }

        @Test
        fun `filter datasets on one spatial with space, spatial = 'Sogn og fjordane'`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(spatial = SearchFilter("Sogn og fjordane"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<Dataset> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            val validValues = listOf("Sogn og fjordane")

            val allThemesValid = result.all { dataset ->
                val spatialCodes = dataset.spatial?.map { it.prefLabel?.nb }
                val datasetValid = spatialCodes?.containsAll(validValues) ?: false
                datasetValid
            }

            Assertions.assertTrue(allThemesValid)
        }
    }

    @Nested
    inner class LosTheme {
        @Test
        fun `filter datasets on multiple los`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(los = SearchFilter("familie-og-barn,demokrati-og-innbyggerrettigheter/politikk-og-valg"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<Dataset> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            val validValues = listOf("familie-og-barn", "demokrati-og-innbyggerrettigheter/politikk-og-valg")

            val allThemesValid = result.all { dataset ->
                val themeCodes = dataset.losTheme?.map { it.losPaths }
                val datasetValid = themeCodes?.containsAll(validValues) ?: false
                datasetValid
            }

            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on non-existing los = '1234' should return nothing`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(los = SearchFilter("1234"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<Dataset> = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.size)
        }
        @Test
        fun `filtering datasets by parent category should include hits from subcategories`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(los = SearchFilter("demokrati-og-innbyggerrettigheter"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            val allThemesValid = result.all { searchObject ->
                searchObject.losTheme?.any { losNode ->
                    losNode.losPaths?.startsWith("demokrati-og-innbyggerrettigheter") ?: false
                } ?: false
            }
            Assertions.assertTrue(allThemesValid)
        }
    }

    @Nested
    inner class OrgPath {
        @Test
        fun `filter datasets on orgPath = 'FYLKE'`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(orgPath = SearchFilter("/FYLKE"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size )

            for (dataset in result) {
                Assertions.assertEquals("/FYLKE", dataset.organization?.orgPath)
            }
        }

        @Test
        fun `filter datasets on non-existing orgPath = '1234' should return nothing`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(orgPath = SearchFilter(value = "/1234"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.size)
        }

        @Test
        fun `filtering datasets by parent category should include hits from subcategories`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(orgPath = SearchFilter("/STAT"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.size)

            val allThemesValid = result.all { dataset ->
                dataset.organization?.orgPath?.startsWith("/STAT") ?: false
            }
            Assertions.assertTrue(allThemesValid)
        }
    }

    @Nested
    inner class FdkFormatPrefixed {
        @Test
        fun `filter datasets on list of formats`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(
                formats = SearchFilter(value = listOf("MEDIA_TYPE tiff", "FILE_TYPE SHP"))
            )))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertTrue(result.isNotEmpty())

            val validValues = listOf("MEDIA_TYPE tiff", "FILE_TYPE SHP")

            val allFormatsValid = result.all { dataset ->
                dataset.fdkFormatPrefixed?.all { validValues.contains(it) } ?: false
            }

            Assertions.assertTrue(allFormatsValid)

        }
        @Test
        fun `filter datasets on non-existing format = '1234' should return nothing`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(
                formats = SearchFilter(value = listOf("1234"))
            )))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: List<SearchObject> = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.size)
        }
    }
}

