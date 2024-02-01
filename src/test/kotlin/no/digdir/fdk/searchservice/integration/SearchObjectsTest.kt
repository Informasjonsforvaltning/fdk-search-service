package no.digdir.fdk.searchservice.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.digdir.fdk.searchservice.model.TempTestDBO
import no.digdir.fdk.searchservice.utils.ApiTestContext
import no.digdir.fdk.searchservice.utils.TEMP_TEST_OBJECT
import no.digdir.fdk.searchservice.utils.apiGet
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    properties = ["spring.profiles.active=test"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(initializers = [ApiTestContext.Initializer::class])
@Tag("integration")
class SearchObjectTest: ApiTestContext() {

    private val mapper = jacksonObjectMapper()


    @Test
    fun getSearch() {
        val response = apiGet(port, "/search", null)
        assertEquals(HttpStatus.OK.value(), response["status"])

        val result: List<TempTestDBO> = mapper.readValue(response["body"] as String)
        assertEquals(listOf(TEMP_TEST_OBJECT), result)
    }
}
