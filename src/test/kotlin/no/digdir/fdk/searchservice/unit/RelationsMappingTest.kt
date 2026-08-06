package no.digdir.fdk.searchservice.unit

import no.digdir.fdk.searchservice.data.CONCEPT_WITH_RELATIONS
import no.digdir.fdk.searchservice.data.DATASERVICE_WITH_RELATIONS
import no.digdir.fdk.searchservice.data.DATASET_WITH_RELATIONS
import no.digdir.fdk.searchservice.data.EVENT_WITH_RELATIONS
import no.digdir.fdk.searchservice.data.INFORMATION_MODEL_WITH_RELATIONS
import no.digdir.fdk.searchservice.data.SERVICE_WITH_RELATIONS
import no.digdir.fdk.searchservice.mapper.getRelations
import no.digdir.fdk.searchservice.model.Relation
import no.digdir.fdk.searchservice.model.RelationType
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Tag("unit")
class RelationsMappingTest {
    @Test
    fun `dataset relations to searchObject relations`() {
        assertEquals(expectedDatasetRelations.sortedBy { it.uri }, DATASET_WITH_RELATIONS.getRelations().sortedBy { it.uri })
    }

    @Test
    fun `event relations to searchObject relations`() {
        assertEquals(expectedEventRelations.sortedBy { it.uri }, EVENT_WITH_RELATIONS.getRelations().sortedBy { it.uri })
    }

    @Test
    fun `information relations to searchObject relations`() {
        assertEquals(
            expectedInformationModelRelations.sortedBy { it.uri },
            INFORMATION_MODEL_WITH_RELATIONS.getRelations().sortedBy { it.uri },
        )
    }

    @Test
    fun `concept relations to searchObject relations`() {
        assertEquals(expectedConceptRelations.sortedBy { it.uri }, CONCEPT_WITH_RELATIONS.getRelations().sortedBy { it.uri })
    }

    @Test
    fun `data service relations to searchObject relations`() {
        assertEquals(expectedDataServiceRelations.sortedBy { it.uri }, DATASERVICE_WITH_RELATIONS.getRelations().sortedBy { it.uri })
    }

    @Test
    fun `service relations to searchObject relations`() {
        assertEquals(expectedServiceRelations.sortedBy { it.uri }, SERVICE_WITH_RELATIONS.getRelations().sortedBy { it.uri })
    }
}

val expectedDatasetRelations =
    listOf(
        Relation(uri = "subject_uri", type = RelationType.SUBJECT),
        Relation(uri = "conformsTo_uri", type = RelationType.CONFORMS_TO),
        Relation(uri = "inSeries_uri", type = RelationType.IN_SERIES),
        Relation(uri = "source_uri", type = RelationType.SOURCE),
        Relation(uri = "has_version_uri", type = RelationType.HAS_VERSION),
        Relation(uri = "is_version_of_uri", type = RelationType.IS_VERSION_OF),
        Relation(uri = "is_part_of_uri", type = RelationType.IS_PART_OF),
        Relation(uri = "has_part_uri", type = RelationType.HAS_PART),
        Relation(uri = "references_uri", type = RelationType.REFERENCES),
        Relation(uri = "is_referenced_by_uri", type = RelationType.IS_REFERENCED_BY),
        Relation(uri = "replaces_uri", type = RelationType.REPLACES),
        Relation(uri = "is_replaced_by_uri", type = RelationType.IS_REPLACED_BY),
        Relation(uri = "requires_uri", type = RelationType.REQUIRES),
        Relation(uri = "is_required_by_uri", type = RelationType.IS_REQUIRED_BY),
        Relation(uri = "relation_uri", type = RelationType.RELATION),
        Relation(uri = "missing_type_uri", type = RelationType.RELATION),
    )

val expectedEventRelations =
    listOf(
        Relation(uri = "subject_uri", type = RelationType.SUBJECT),
    )

val expectedInformationModelRelations =
    listOf(
        Relation(uri = "replaces_uri", type = RelationType.REPLACES),
        Relation(uri = "hasPart_uri", type = RelationType.HAS_PART),
        Relation(uri = "isReplacedBy_uri", type = RelationType.IS_REPLACED_BY),
        Relation(uri = "isPartOf_uri", type = RelationType.IS_PART_OF),
        Relation(uri = "subjects_uri", type = RelationType.SUBJECT),
    )

val expectedConceptRelations =
    listOf(
        Relation(uri = "memberOf_uri", type = RelationType.MEMBER_OF),
        Relation(uri = "replaces_uri", type = RelationType.REPLACES),
        Relation(uri = "seeAlso_uri", type = RelationType.SEE_ALSO),
        Relation(uri = "associativeRelation_uri", type = RelationType.ASSOCIATIVE_RELATION),
        Relation(uri = "partitiveRelation_isPartOf_uri", type = RelationType.IS_PART_OF),
        Relation(uri = "partitiveRelation_hasPart_uri", type = RelationType.HAS_PART),
        Relation(uri = "isReplacedBy_uri", type = RelationType.IS_REPLACED_BY),
        Relation(uri = "closeMatch_uri", type = RelationType.CLOSE_MATCH),
        Relation(uri = "exactMatch_uri", type = RelationType.EXACT_MATCH),
        Relation(uri = "genericRelation_specializes_uri", type = RelationType.SPECIALIZES),
        Relation(uri = "genericRelation_generalizes_uri", type = RelationType.GENERALIZES),
    )

val expectedDataServiceRelations =
    listOf(
        Relation(uri = "servesDataset_uri", type = RelationType.SERVES_DATASET),
        Relation(uri = "conformsTo_uri", type = RelationType.CONFORMS_TO),
    )

val expectedServiceRelations =
    listOf(
        Relation(uri = "subject_uri", type = RelationType.SUBJECT),
        Relation(uri = "relation_uri", type = RelationType.RELATION),
        Relation(uri = "requires_uri", type = RelationType.REQUIRES),
        Relation(uri = "isDescribedAt_uri", type = RelationType.IS_DESCRIBED_AT),
        Relation(uri = "isGroupedBy_uri", type = RelationType.IS_GROUPED_BY),
        Relation(uri = "isClassifiedBy_uri", type = RelationType.IS_CLASSIFIED_BY),
    )
