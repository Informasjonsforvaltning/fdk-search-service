package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.SearchFilter
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.model.SearchResult
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.utils.ApiTestContext
import no.digdir.fdk.searchservice.utils.createEmptySearchFilters
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
    private val DATASETS_PATH = "/search/datasets"
    private val SEARCH_QUERY = "test"
    private val SEARCH_QUERY_NO_HITS = "nohits"
    private val SEARCH_QUERYS_HIT_ALL_SEARCH_FIELDS =
        listOf("title", "description", "keyword")
    private val mapper = jacksonObjectMapper()
    private val searchFilters = createEmptySearchFilters()

    @Test
    fun `search datasets with at least one hit`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY, searchFilters))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(result.hits.size > 0)
    }

    @Test
    fun `check searchType`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY, searchFilters))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        result.hits.forEach {
            Assertions.assertTrue(it.searchType == SearchType.DATASET)
        }
    }

    @Test
    fun `search datasets with no hits`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(SEARCH_QUERY_NO_HITS, searchFilters))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertEquals(0, result.hits.size)
    }

    @Test
    fun `search datasets with empty query`() {
        val searchBody = mapper.writeValueAsString(SearchOperation("", searchFilters))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertNotEquals(0, result.hits.size)
    }

    @Test
    fun `search and hit all datasets search fields successfully`() {
        SEARCH_QUERYS_HIT_ALL_SEARCH_FIELDS.forEach {
            val searchBody = mapper.writeValueAsString(SearchOperation(it, searchFilters))
            val response = requestApi(DATASETS_PATH, port, searchBody, POST)
            Assertions.assertEquals(200, response["status"])

            val result: SearchResult = mapper.readValue(response["body"] as String)
            if (result.hits.isEmpty()) Assertions.fail<String>("No hit for query: $it")
        }
    }

    @Test
    fun `datasets with distributions with unknown format type is indexed as fdkFormat=UNKNOWN`() {
        val searchBody = mapper.writeValueAsString(SearchOperation(filters = searchFilters.copy(formats = SearchFilter(value = listOf("UNKNOWN")))))
        val response = requestApi(DATASETS_PATH, port, searchBody, POST)
        Assertions.assertEquals(200, response["status"])

        val result: SearchResult = mapper.readValue(response["body"] as String)
        Assertions.assertTrue(result.hits.isNotEmpty())
        result.hits.forEach { hit ->
            Assertions.assertFalse(hit.fdkFormatPrefixed?.filter { it == "UNKNOWN" }.isNullOrEmpty())
        }
    }
}
