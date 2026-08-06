package no.digdir.fdk.searchservice.unit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.digdir.fdk.searchservice.model.SearchOperation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class SearchOperationDeserializationTest {
    private val mapper = jacksonObjectMapper()

    @Test
    fun `fields=null does not fail and falls back to defaults`() {
        val json =
            """
            {
              "query": "test",
              "filters": null,
              "fields": null,
              "sort": null,
              "pagination": { "size": 10, "page": 0 },
              "profile": null
            }
            """.trimIndent()

        val op: SearchOperation = mapper.readValue(json, SearchOperation::class.java)
        // Deserialization should succeed and SearchService will fall back when fields is null.
        assertNotNull(op)
        Assertions.assertNull(op.fields)
    }

    @Test
    fun `pagination nulls do not fail`() {
        val json =
            """
            {
              "pagination": { "page": null, "size": null }
            }
            """.trimIndent()

        val op: SearchOperation = mapper.readValue(json, SearchOperation::class.java)
        assertNotNull(op.pagination)
        // Kotlin model keeps defaults when null is provided.
        Assertions.assertEquals(0, op.pagination.getPage())
        Assertions.assertEquals(10, op.pagination.getSize())
    }

    @Test
    fun `numeric filter value nulls do not fail`() {
        val json =
            """
            {
              "filters": {
                "lastXDays": { "value": null }
              }
            }
            """.trimIndent()

        val op: SearchOperation = mapper.readValue(json, SearchOperation::class.java)
        Assertions.assertNotNull(op.filters)
        Assertions.assertNotNull(op.filters?.lastXDays)
        Assertions.assertNull(op.filters?.lastXDays?.value)
    }

    @Test
    fun `fields title null does not fail`() {
        val json =
            """
            {
              "fields": { "title": null }
            }
            """.trimIndent()

        val op: SearchOperation = mapper.readValue(json, SearchOperation::class.java)
        Assertions.assertNotNull(op.fields)
        Assertions.assertNull(op.fields?.title)
    }
}
