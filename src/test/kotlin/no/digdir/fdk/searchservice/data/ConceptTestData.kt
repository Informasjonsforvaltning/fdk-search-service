package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*
import no.digdir.fdk.searchservice.model.Collection

// ids start from 20 to avoid conflicts with the ids in the test data for the other types
val TEST_NULL_CONCEPT = Concept(
    id = "1020",
    identifier = "identifier 1020",
    publisher = null,
    definition = null,
    prefLabel = null,
    harvest = null,
    collection = null,
    memberOf = null,
    replaces = null,
    seeAlso = null,
    associativeRelation = null,
    partitiveRelation = null,
    isReplacedBy = null,
    closeMatch = null,
    exactMatch = null,
    genericRelation = null)

val TEST_CONCEPT_HIT_ALL_FIELDS = TEST_NULL_CONCEPT.copy(
    id = "1021",
    identifier = "identifier 1021",
    publisher = Organization(
        orgPath = "/STAT/102117858",
        identifier = "Test publisher > identifier 1021",
        uri = "Test publisher > uri 1021",
        name = "Test publisher > name",
        prefLabel = LocalizedStrings(
            "NB Test publisher > prefLabel",
            "NN Test publisher > prefLabel",
            "EN Test publisher > prefLabel"),
    ),
    definition = Definition(
        LocalizedStrings(
            "NB Test definition > text",
            "NN Test definition > text",
            "EN Test definition > text"),
        Sources(listOf(TextAndURI(LocalizedStrings(
            "NB Test definition > sources > text",
            "NN Test definition > sources > text",
            "EN Test definition > sources > text")))),
        "Test definition > sourceRelationship"),
    prefLabel = LocalizedStrings(
        "NB Test prefLabel, title",
        "NN Test prefLabel",
        "EN Test prefLabel"),
    harvest =  HarvestMetadata(
            "Test harvest > firstHarvested",
            listOf("Test harvest > changed")),
    collection = Collection(
        id = "Test collection > id 1021",
        uri = "Test collection > uri 1021",
        label = LocalizedStrings(
            "NB Test collection > label",
            "NN Test collection > label",
            "EN Test collection > label"),
        description = LocalizedStrings(
            "NB Test collection > description",
            "NN Test collection > description",
            "EN Test collection > description"),
        publisher = Organization(
            orgPath = "/KOMMUNE/102117858",
            identifier = "Test publisher > identifier",
            uri = "Test publisher > uri ",
            name = "Test publisher > name",
            prefLabel = LocalizedStrings(
                "NB Test publisher > prefLabel",
                "NN Test publisher > prefLabel",
            "EN Test publisher > prefLabel"))
    ),
)

val CONCEPT_WITH_RELATIONS = TEST_NULL_CONCEPT.copy(
    collection = Collection(description = null, id = null, publisher = null, label = null, uri = "collection_uri"),
    memberOf = listOf("memberOf_uri"),
    replaces = listOf("replaces_uri"),
    seeAlso = listOf("seeAlso_uri"),
    associativeRelation = listOf(AssociativeRelation(description = null, related = "associativeRelation_uri")),
    partitiveRelation = listOf(
        PartitiveRelation(description = null, hasPart = null, isPartOf = "partitiveRelation_isPartOf_uri"),
        PartitiveRelation(description = null, hasPart = "partitiveRelation_hasPart_uri", isPartOf = null)
    ),
    isReplacedBy = listOf("isReplacedBy_uri"),
    closeMatch = listOf("closeMatch_uri"),
    exactMatch = listOf("exactMatch_uri"),
    genericRelation = listOf(
        GenericRelation(divisioncriterion = null, generalizes = null, specializes = "genericRelation_specializes_uri"),
        GenericRelation(divisioncriterion = null, generalizes = "genericRelation_generalizes_uri", specializes = null)
    )
)
