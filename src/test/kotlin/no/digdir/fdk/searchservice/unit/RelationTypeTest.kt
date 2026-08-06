package no.digdir.fdk.searchservice.unit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.digdir.fdk.searchservice.elastic.RelationTypeToStringConverter
import no.digdir.fdk.searchservice.elastic.StringToRelationTypeConverter
import no.digdir.fdk.searchservice.model.Relation
import no.digdir.fdk.searchservice.model.RelationType
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Tag("unit")
class RelationTypeTest {
    private val objectMapper = jacksonObjectMapper()
    private val writingConverter = RelationTypeToStringConverter()
    private val readingConverter = StringToRelationTypeConverter()

    @Test
    fun `fromValue accepts camelCase values stored in Elasticsearch`() {
        assertEquals(RelationType.SUBJECT, RelationType.fromValue("subject"))
        assertEquals(RelationType.ASSOCIATIVE_RELATION, RelationType.fromValue("associativeRelation"))
        assertEquals(RelationType.IS_REPLACED_BY, RelationType.fromValue("isReplacedBy"))
    }

    @Test
    fun `fromValue accepts enum names written before converters were registered`() {
        assertEquals(RelationType.SUBJECT, RelationType.fromValue("SUBJECT"))
        assertEquals(RelationType.CONFORMS_TO, RelationType.fromValue("CONFORMS_TO"))
    }

    @Test
    fun `fromValue rejects unknown values`() {
        assertThrows<IllegalArgumentException> {
            RelationType.fromValue("unknownRelation")
        }
    }

    @Test
    fun `elasticsearch converters round-trip camelCase values`() {
        RelationType.entries.forEach { type ->
            val written = writingConverter.convert(type)
            assertEquals(type.value, written)
            assertEquals(type, readingConverter.convert(written))
        }
    }

    @Test
    fun `jackson serializes relation type as camelCase`() {
        val json = objectMapper.writeValueAsString(Relation(uri = "http://example.com", type = RelationType.SUBJECT))
        assertTrue(json.contains("\"type\":\"subject\""))
    }

    @Test
    fun `jackson deserializes camelCase relation type`() {
        val relation =
            objectMapper.readValue(
                """{"uri":"http://example.com","type":"subject"}""",
                Relation::class.java,
            )
        assertEquals(RelationType.SUBJECT, relation.type)
    }
}
