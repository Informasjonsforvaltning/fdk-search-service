package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.data.TEST_DATASET_FILTERS
import no.digdir.fdk.searchservice.model.*
import no.digdir.fdk.searchservice.utils.ApiTestContext
import no.digdir.fdk.searchservice.utils.createEmptySearchFilters
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
class FilterTest : ApiTestContext() {
    private val mapper = jacksonObjectMapper()
    private val SEARCH_FILTER = createEmptySearchFilters()
    private val DATASETS_PATH = "/search/datasets"
    private val DATASERVICES_PATH = "/search/dataservices"
    private val ALL_RESOURCES_PATH = "/search"

    @Nested
    inner class IsOpen {
        @Test
        fun `filter datasets on isOpen = true`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(openData = SearchFilter(true))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            for (dataset in result.hits) {
                Assertions.assertTrue(dataset.isOpenData ?: false)
            }
        }

        @Test
        fun `filter datasets on isOpen = false`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(openData = SearchFilter(false))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            for (dataset in result.hits) {
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

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            for (dataset in result.hits) {
                Assertions.assertTrue(dataset.accessRights?.code?.contains("PUBLIC") ?: false)
            }
        }

        @Test
        fun `filter datasets on non valid accessRight returns empty list`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(accessRights = SearchFilter(""))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.hits.size)
        }
    }

    @Nested
    inner class DataTheme {
        @Test
        fun `filter datasets on one theme, theme = 'REGI'`() {
            val searchBody = mapper.writeValueAsString(
                SearchOperation(
                    filters = SEARCH_FILTER.copy(
                        dataTheme = SearchFilter(
                            listOf("REGI")
                        )
                    )
                )
            )
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertTrue(result.hits.isNotEmpty())

            val validValues = listOf("REGI")

            val allThemesValid = result.hits.all { dataset ->
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
                            dataTheme = SearchFilter(
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

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = listOf("ENVI", "REGI")

            val allThemesValid = result.hits.all { dataset ->
                val themeCodes = dataset.dataTheme?.map { it.code }
                val datasetValid = themeCodes?.containsAll(validValues) ?: false
                datasetValid
            }

            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on non-existing theme = '1234' should return nothing`() {
            val searchBody = mapper.writeValueAsString(
                SearchOperation(filters = SEARCH_FILTER.copy(dataTheme = SearchFilter(listOf("1234"))))
            )
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.hits.size)
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

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            for (dataset in result.hits) {
                Assertions.assertEquals("BRUKER", dataset.provenance?.code)
            }
        }

        @Test
        fun `filter datasets on non valid provenance = '1234' should return nothing`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(provenance = SearchFilter("1234"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.hits.size)
        }
    }

    @Nested
    inner class Spatial {
        @Test
        fun `filter datasets on one spatial, spatial = 'Norge'`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(
                spatial = SearchFilter(listOf("Norge")))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = listOf("Norge")

            val allThemesValid = result.hits.all { dataset ->
                val spatialCodes = dataset.spatial?.map { it.prefLabel?.nb }
                val datasetValid = spatialCodes?.containsAll(validValues) ?: false
                datasetValid
            }

            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on multiple spatial, spatial = 'Norge,Spania'`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(
                    spatial = SearchFilter(listOf("Norge", "Spania")))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = listOf("Norge", "Spania")

            val allThemesValid = result.hits.all { dataset ->
                val spatialCodes = dataset.spatial?.map { it.prefLabel?.nb }
                val datasetValid = spatialCodes?.containsAll(validValues) ?: false
                datasetValid
            }

            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on non-existing spatial = '1234' should return nothing`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(
                spatial = SearchFilter(listOf("1234")))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.hits.size)
        }

        @Test
        fun `filter datasets on one spatial with space, spatial = 'Sogn og fjordane'`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(
                spatial = SearchFilter(listOf("Sogn og fjordane")))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = listOf("Sogn og fjordane")

            val allThemesValid = result.hits.all { dataset ->
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
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(losTheme = SearchFilter(
                    listOf("familie-og-barn", "demokrati-og-innbyggerrettigheter/politikk-og-valg")))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = listOf("familie-og-barn", "demokrati-og-innbyggerrettigheter/politikk-og-valg")

            val allThemesValid = result.hits.all { dataset ->
                val themeCodes = dataset.losTheme?.flatMap { it.losPaths ?: emptySet() }
                val datasetValid = themeCodes?.containsAll(validValues) ?: false
                datasetValid
            }

            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on non-existing los = '1234' should return nothing`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(losTheme = SearchFilter(
                listOf("1234")))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.hits.size)
        }

        @Test
        fun `filtering datasets by parent category should include hits from subcategories`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(losTheme = SearchFilter(listOf("demokrati-og-innbyggerrettigheter")))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            val allThemesValid = result.hits.all { searchObject ->
                searchObject.losTheme?.any { losNode ->
                    losNode.losPaths?.any { losPath -> losPath.startsWith("demokrati-og-innbyggerrettigheter") }
                        ?: false
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

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            for (dataset in result.hits) {
                Assertions.assertEquals("/FYLKE", dataset.organization?.orgPath)
            }
        }

        @Test
        fun `filter datasets on non-existing orgPath = '1234' should return nothing`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(orgPath = SearchFilter(value = "/1234"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.hits.size)
        }

        @Test
        fun `filtering datasets by parent category should include hits from subcategories`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(orgPath = SearchFilter("/STAT"))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            val allThemesValid = result.hits.all { dataset ->
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

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertTrue(result.hits.isNotEmpty())

            val validValues = listOf("MEDIA_TYPE tiff", "FILE_TYPE SHP", "UNKNOWN")

            val allFormatsValid = result.hits.all { dataset ->
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

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.hits.size)
        }

        @Test
        fun `filter data services on format`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(
                formats = SearchFilter(value = listOf("MEDIA_TYPE turtle"))
            )))
            val response = requestApi(DATASERVICES_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertTrue(result.hits.isNotEmpty())

            val validValues = listOf("MEDIA_TYPE turtle", "UNKNOWN")

            val allFormatsValid = result.hits.all { dataset ->
                dataset.fdkFormatPrefixed?.all { validValues.contains(it) } ?: false
            }

            Assertions.assertTrue(allFormatsValid)
        }
    }

    @Nested
    inner class Relations {
        @Test
        fun `get relations to dataset`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(
                relations = SearchFilter(TEST_DATASET_FILTERS.uri))
            ))

            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = result.hits.all { searchObject ->
                searchObject.relations?.any { relation ->
                    relation.uri == TEST_DATASET_FILTERS.uri
                } ?: false
            }

            Assertions.assertTrue(validValues)
        }

        @Test
        fun `filter datasets on non-existing uri = '1234' should return nothing`() {
            val searchBody = mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(
                relations = SearchFilter("1234"))
            ))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertTrue(result.hits.isEmpty())
        }
    }

    @Nested
    inner class Last_x_days {
        @Test
        fun `filter datasets on harvested last 7 days`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(lastXDays = SearchFilter(7))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)
        }

        @Test
        fun `filter datasets on harvested 1 day ago should return no hits`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(lastXDays = SearchFilter(1))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.hits.size)
        }
    }

    @Nested
    inner class Last_x_days_modified {
        @Test
        fun `filter datasets on modified last 7 days`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(lastXDaysModified = SearchFilter(7))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)
        }

        @Test
        fun `filter datasets on modified 1 day ago should return no hits`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(lastXDaysModified = SearchFilter(1))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.hits.size)
        }
    }

    @Nested
    inner class Uris {
        @Test
        fun `filter datasets by uri dataset uri 2`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(uri = SearchFilter(listOf("dataset.uri.2")))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertNotEquals(0, result.hits.size)
        }

        @Test
        fun `filter by uri should return no hits`() {
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(uri = SearchFilter(listOf("dataset.uri.doesNotExist")))))
            val response = requestApi(DATASETS_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(0, result.hits.size)
        }

        @Test
        fun `filter by several uris`() {
            val uris = listOf("dataset.uri.2", "concept.uri.0")
            val searchBody =
                mapper.writeValueAsString(SearchOperation(filters = SEARCH_FILTER.copy(uri = SearchFilter(uris))))
            val response = requestApi(ALL_RESOURCES_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertEquals(2, result.hits.size)
            Assertions.assertTrue(result.hits.map { it.uri }.containsAll(uris))
        }
    }

    @Nested
    inner class Sorting {
        @Test
        fun `sorting on descending firstHarvested returns correct order`() {
            val searchBody = mapper.writeValueAsString(
                SearchOperation(
                    sort = SortField(
                        field = SortFieldEnum.FIRST_HARVESTED, direction = SortDirection.DESC)
                )
            )

            val response = requestApi(ALL_RESOURCES_PATH, port, searchBody, HttpMethod.POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            Assertions.assertTrue(1 < result.hits.size)

            val expectedResult = result.hits.sortedByDescending { it.metadata?.firstHarvested }

            Assertions.assertTrue(expectedResult == result.hits)
        }
    }
}
