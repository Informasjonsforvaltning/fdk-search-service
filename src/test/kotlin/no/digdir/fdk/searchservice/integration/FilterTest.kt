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

    private fun search(operation: SearchOperation, path: String = DATASETS_PATH): SearchResult {
        val searchBody = mapper.writeValueAsString(operation)
        val response = requestApi(path, port, searchBody, HttpMethod.POST)
        Assertions.assertEquals(200, response["status"])
        return mapper.readValue(response["body"] as String)
    }

    private fun searchWithFilters(filters: SearchFilters, path: String = DATASETS_PATH): SearchResult =
        search(SearchOperation(filters = filters), path)

    @Nested
    inner class IsOpen {
        @Test
        fun `filter datasets on isOpen = true`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(openData = SearchFilter(true)))
            Assertions.assertNotEquals(0, result.hits.size)

            for (dataset in result.hits) {
                Assertions.assertTrue(dataset.isOpenData ?: false)
            }
        }

        @Test
        fun `filter datasets on isOpen = false`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(openData = SearchFilter(false)))
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
            val result = searchWithFilters(SEARCH_FILTER.copy(accessRights = SearchFilter("PUBLIC")))
            Assertions.assertNotEquals(0, result.hits.size)

            for (dataset in result.hits) {
                Assertions.assertTrue(dataset.accessRights?.code?.contains("PUBLIC") ?: false)
            }
        }

        @Test
        fun `filter datasets on non valid accessRight returns empty list`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(accessRights = SearchFilter("")))
            Assertions.assertEquals(0, result.hits.size)
        }
    }

    @Nested
    inner class DataTheme {
        @Test
        fun `filter datasets on one theme, theme = 'REGI'`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(dataTheme = SearchFilter(listOf("REGI"))))
            Assertions.assertTrue(result.hits.isNotEmpty())

            val validValues = listOf("REGI")
            val allThemesValid = result.hits.all { dataset ->
                dataset.dataTheme?.map { it.code }?.containsAll(validValues) ?: false
            }
            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on multiple themes, theme = 'ENVI,REGI'`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(dataTheme = SearchFilter(listOf("ENVI", "REGI"))))
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = listOf("ENVI", "REGI")
            val allThemesValid = result.hits.all { dataset ->
                dataset.dataTheme?.map { it.code }?.containsAll(validValues) ?: false
            }
            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on non-existing theme = '1234' should return nothing`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(dataTheme = SearchFilter(listOf("1234"))))
            Assertions.assertEquals(0, result.hits.size)
        }
    }


    @Nested
    inner class Provenance {
        @Test
        fun `filter datasets on provenance = 'BRUKER'`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(provenance = SearchFilter("BRUKER")))
            Assertions.assertNotEquals(0, result.hits.size)

            for (dataset in result.hits) {
                Assertions.assertEquals("BRUKER", dataset.provenance?.code)
            }
        }

        @Test
        fun `filter datasets on non valid provenance = '1234' should return nothing`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(provenance = SearchFilter("1234")))
            Assertions.assertEquals(0, result.hits.size)
        }
    }

    @Nested
    inner class Spatial {
        @Test
        fun `filter datasets on one spatial, spatial = 'Norge'`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(spatial = SearchFilter(listOf("Norge"))))
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = listOf("Norge")
            val allThemesValid = result.hits.all { dataset ->
                dataset.spatial?.map { it.prefLabel?.nb }?.containsAll(validValues) ?: false
            }
            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on multiple spatial, spatial = 'Norge,Spania'`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(spatial = SearchFilter(listOf("Norge", "Spania"))))
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = listOf("Norge", "Spania")
            val allThemesValid = result.hits.all { dataset ->
                dataset.spatial?.map { it.prefLabel?.nb }?.containsAll(validValues) ?: false
            }
            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on non-existing spatial = '1234' should return nothing`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(spatial = SearchFilter(listOf("1234"))))
            Assertions.assertEquals(0, result.hits.size)
        }

        @Test
        fun `filter datasets on one spatial with space, spatial = 'Sogn og fjordane'`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(spatial = SearchFilter(listOf("Sogn og fjordane"))))
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = listOf("Sogn og fjordane")
            val allThemesValid = result.hits.all { dataset ->
                dataset.spatial?.map { it.prefLabel?.nb }?.containsAll(validValues) ?: false
            }
            Assertions.assertTrue(allThemesValid)
        }
    }

    @Nested
    inner class LosTheme {
        @Test
        fun `filter datasets on multiple los`() {
            val validValues = listOf("familie-og-barn", "demokrati-og-innbyggerrettigheter/politikk-og-valg")
            val result = searchWithFilters(SEARCH_FILTER.copy(losTheme = SearchFilter(validValues)))
            Assertions.assertNotEquals(0, result.hits.size)

            val allThemesValid = result.hits.all { dataset ->
                dataset.losTheme?.flatMap { it.losPaths ?: emptySet() }?.containsAll(validValues) ?: false
            }
            Assertions.assertTrue(allThemesValid)
        }

        @Test
        fun `filter datasets on non-existing los = '1234' should return nothing`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(losTheme = SearchFilter(listOf("1234"))))
            Assertions.assertEquals(0, result.hits.size)
        }

        @Test
        fun `filtering datasets by parent category should include hits from subcategories`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(losTheme = SearchFilter(listOf("demokrati-og-innbyggerrettigheter"))))
            Assertions.assertNotEquals(0, result.hits.size)

            val allThemesValid = result.hits.all { searchObject ->
                searchObject.losTheme?.any { losNode ->
                    losNode.losPaths?.any { losPath -> losPath.startsWith("demokrati-og-innbyggerrettigheter") } ?: false
                } ?: false
            }
            Assertions.assertTrue(allThemesValid)
        }
    }

    @Nested
    inner class OrgPath {
        @Test
        fun `filter datasets on orgPath = 'FYLKE'`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(orgPath = SearchFilter("/FYLKE")))
            Assertions.assertNotEquals(0, result.hits.size)

            for (dataset in result.hits) {
                Assertions.assertEquals("/FYLKE", dataset.organization?.orgPath)
            }
        }

        @Test
        fun `filter datasets on non-existing orgPath = '1234' should return nothing`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(orgPath = SearchFilter(value = "/1234")))
            Assertions.assertEquals(0, result.hits.size)
        }

        @Test
        fun `filtering datasets by parent category should include hits from subcategories`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(orgPath = SearchFilter("/STAT")))
            Assertions.assertNotEquals(0, result.hits.size)

            val allThemesValid = result.hits.all { dataset -> dataset.organization?.orgPath?.startsWith("/STAT") ?: false }
            Assertions.assertTrue(allThemesValid)
        }
    }

    @Nested
    inner class FdkFormatPrefixed {
        @Test
        fun `filter datasets on list of formats`() {
            val result = searchWithFilters(
                SEARCH_FILTER.copy(formats = SearchFilter(value = listOf("MEDIA_TYPE tiff", "FILE_TYPE SHP")))
            )
            Assertions.assertTrue(result.hits.isNotEmpty())

            val validValues = listOf("MEDIA_TYPE tiff", "FILE_TYPE SHP", "UNKNOWN")
            val allFormatsValid = result.hits.all { dataset -> dataset.fdkFormatPrefixed?.all { validValues.contains(it) } ?: false }
            Assertions.assertTrue(allFormatsValid)
        }

        @Test
        fun `filter datasets on non-existing format = '1234' should return nothing`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(formats = SearchFilter(value = listOf("1234"))))
            Assertions.assertEquals(0, result.hits.size)
        }

        @Test
        fun `filter data services on format`() {
            val result = searchWithFilters(
                SEARCH_FILTER.copy(formats = SearchFilter(value = listOf("MEDIA_TYPE turtle"))),
                DATASERVICES_PATH
            )
            Assertions.assertTrue(result.hits.isNotEmpty())

            val validValues = listOf("MEDIA_TYPE turtle", "UNKNOWN")
            val allFormatsValid = result.hits.all { dataset -> dataset.fdkFormatPrefixed?.all { validValues.contains(it) } ?: false }
            Assertions.assertTrue(allFormatsValid)
        }
    }

    @Nested
    inner class Relations {
        @Test
        fun `get relations to dataset`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(relations = SearchFilter(TEST_DATASET_FILTERS.uri!!)))
            Assertions.assertNotEquals(0, result.hits.size)

            val validValues = result.hits.all { searchObject ->
                searchObject.relations?.any { relation -> relation.uri == TEST_DATASET_FILTERS.uri } ?: false
            }
            Assertions.assertTrue(validValues)
        }

        @Test
        fun `filter datasets on non-existing uri = '1234' should return nothing`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(relations = SearchFilter("1234")))
            Assertions.assertTrue(result.hits.isEmpty())
        }
    }

    @Nested
    inner class Last_x_days {
        @Test
        fun `filter datasets on harvested last 7 days`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(lastXDays = SearchFilter(7)))
            Assertions.assertNotEquals(0, result.hits.size)
        }

        @Test
        fun `filter datasets on harvested 1 day ago should return no hits`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(lastXDays = SearchFilter(1)))
            Assertions.assertEquals(0, result.hits.size)
        }
    }

    @Nested
    inner class Last_x_days_modified {
        @Test
        fun `filter datasets on modified last 7 days`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(lastXDaysModified = SearchFilter(7)))
            Assertions.assertNotEquals(0, result.hits.size)
        }

        @Test
        fun `filter datasets on modified 1 day ago should return no hits`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(lastXDaysModified = SearchFilter(1)))
            Assertions.assertEquals(0, result.hits.size)
        }
    }

    @Nested
    inner class Uris {
        @Test
        fun `filter datasets by uri dataset uri 2`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(uri = SearchFilter(listOf("dataset.uri.2"))))
            Assertions.assertNotEquals(0, result.hits.size)
        }

        @Test
        fun `filter by uri should return no hits`() {
            val result = searchWithFilters(SEARCH_FILTER.copy(uri = SearchFilter(listOf("dataset.uri.doesNotExist"))))
            Assertions.assertEquals(0, result.hits.size)
        }

        @Test
        fun `filter by several uris`() {
            val uris = listOf("dataset.uri.2", "concept.uri.0")
            val result = searchWithFilters(SEARCH_FILTER.copy(uri = SearchFilter(uris)), ALL_RESOURCES_PATH)
            Assertions.assertEquals(2, result.hits.size)
            Assertions.assertTrue(result.hits.map { it.uri }.containsAll(uris))
        }
    }

    @Nested
    inner class Sorting {
        @Test
        fun `sorting on descending firstHarvested returns correct order`() {
            val operation = SearchOperation(sort = SortField(field = SortFieldEnum.FIRST_HARVESTED, direction = SortDirection.DESC))
            val result = search(operation, ALL_RESOURCES_PATH)
            Assertions.assertTrue(1 < result.hits.size)

            val expectedResult = result.hits.sortedByDescending { it.metadata?.firstHarvested }
            Assertions.assertTrue(expectedResult == result.hits)
        }
    }
}
