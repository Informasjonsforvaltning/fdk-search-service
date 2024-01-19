package no.digdir.fdk.searchservice.integration


import no.digdir.fdk.searchservice.utils.apiGet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Tag("integration")

class StatusTest {

    @LocalServerPort
    var port = 0

    @Test
    fun ping() {
        val response = apiGet(port, "/ping", null)
        assertEquals(HttpStatus.OK.value(), response["status"])
    }

    @Test
    fun ready() {
        val response = apiGet(port, "/ready", null)
        assertEquals(HttpStatus.OK.value(), response["status"])
    }
}
