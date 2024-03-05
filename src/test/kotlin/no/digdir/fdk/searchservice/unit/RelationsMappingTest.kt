package no.digdir.fdk.searchservice.unit

import no.digdir.fdk.searchservice.data.*
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
        assertEquals(expectedInformationModelRelations.sortedBy { it.uri }, INFORMATION_MODEL_WITH_RELATIONS.getRelations().sortedBy { it.uri })
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

val expectedDatasetRelations = listOf(
    Relation(uri = "subject_uri", type=RelationType.subject),
    Relation(uri = "conformsTo_uri", type=RelationType.conformsTo),
    Relation(uri = "inSeries_uri", type=RelationType.inSeries),
    Relation(uri = "source_uri", type=RelationType.source),
    Relation(uri = "has_version_uri", type=RelationType.hasVersion),
    Relation(uri = "is_version_of_uri", type=RelationType.isVersionOf),
    Relation(uri = "is_part_of_uri", type=RelationType.isPartOf),
    Relation(uri = "has_part_uri", type=RelationType.hasPart),
    Relation(uri = "references_uri", type=RelationType.references),
    Relation(uri = "is_referenced_by_uri", type=RelationType.isReferencedBy),
    Relation(uri = "replaces_uri", type=RelationType.replaces),
    Relation(uri = "is_replaced_by_uri", type=RelationType.isReplacedBy),
    Relation(uri = "requires_uri", type=RelationType.requires),
    Relation(uri = "is_required_by_uri", type=RelationType.isRequiredBy),
    Relation(uri = "relation_uri", type=RelationType.relation),
    Relation(uri = "missing_type_uri", type=RelationType.relation)
)

val expectedEventRelations = listOf(
    Relation(uri = "subject_uri", type=RelationType.subject)
)

val expectedInformationModelRelations = listOf(
    Relation(uri = "replaces_uri", type=RelationType.replaces),
    Relation(uri = "hasPart_uri", type=RelationType.hasPart),
    Relation(uri = "isReplacedBy_uri", type=RelationType.isReplacedBy),
    Relation(uri = "isPartOf_uri", type=RelationType.isPartOf),
    Relation(uri = "subjects_uri", type=RelationType.subject)
)

val expectedConceptRelations = listOf(
    Relation(uri = "memberOf_uri", type=RelationType.memberOf),
    Relation(uri = "replaces_uri", type=RelationType.replaces),
    Relation(uri = "seeAlso_uri", type=RelationType.seeAlso),
    Relation(uri = "associativeRelation_uri", type=RelationType.associativeRelation),
    Relation(uri = "partitiveRelation_isPartOf_uri", type=RelationType.isPartOf),
    Relation(uri = "partitiveRelation_hasPart_uri", type=RelationType.hasPart),
    Relation(uri = "isReplacedBy_uri", type=RelationType.isReplacedBy),
    Relation(uri = "closeMatch_uri", type=RelationType.closeMatch),
    Relation(uri = "exactMatch_uri", type=RelationType.exactMatch),
    Relation(uri = "genericRelation_specializes_uri", type=RelationType.specializes),
    Relation(uri = "genericRelation_generalizes_uri", type=RelationType.generalizes)
)

val expectedDataServiceRelations = listOf(
    Relation(uri = "servesDataset_uri", type=RelationType.servesDataset),
    Relation(uri = "conformsTo_uri", type=RelationType.conformsTo),
)

val expectedServiceRelations = listOf(
    Relation(uri = "subject_uri", type=RelationType.subject),
    Relation(uri = "relation_uri", type=RelationType.relation),
    Relation(uri = "requires_uri", type=RelationType.requires),
    Relation(uri = "isDescribedAt_uri", type=RelationType.isDescribedAt),
    Relation(uri = "isGroupedBy_uri", type=RelationType.isGroupedBy),
    Relation(uri = "isClassifiedBy_uri", type=RelationType.isClassifiedBy),
)
