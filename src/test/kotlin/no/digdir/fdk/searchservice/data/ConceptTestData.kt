package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*
import no.digdir.fdk.searchservice.model.Collection

// ids start from 20 to avoid conflicts with the ids in the test data for the other types
val TEST_NULL_CONCEPT = Concept(
    identifier = "null concept uri",
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
    publisher = Organization(
        orgPath = "/STAT/102117858",
        id = "Test publisher > identifier 1021",
        uri = "Test publisher > uri 1021",
        name = "Test publisher > name",
        prefLabel = LocalizedStrings(
            "NB Test publisher > prefLabel",
            "NN Test publisher > prefLabel",
            "NO Test publisher > prefLabel",
            "EN Test publisher > prefLabel"),
    ),
    definition = Definition(
        LocalizedStrings(
            "NB Test definition > text",
            "NN Test definition > text",
            "NO Test definition > text",
            "EN Test definition > text"),
        listOf(TextAndURI(LocalizedStrings(
            "NB Test definition > sources > text",
            "NN Test definition > sources > text",
            "NO Test definition > sources > text",
            "EN Test definition > sources > text"))),
        "Test definition > sourceRelationship"),
    prefLabel = LocalizedStrings(
        "NB Test prefLabel, title",
        "NN Test prefLabel",
        "NO Test prefLabel, title",
        "EN Test prefLabel"),
    harvest = HarvestMetadata(
        "2022-02-15T11:00:05Z",
        listOf("Test harvest > changed")),
    collection = Collection(
        id = "Test collection > id 1021",
        uri = "Test collection > uri 1021",
        label = LocalizedStrings(
            "NB Test collection > label",
            "NN Test collection > label",
            "NO Test collection > label",
            "EN Test collection > label"),
        description = LocalizedStrings(
            "NB Test collection > description",
            "NN Test collection > description",
            "NO Test collection > description",
            "EN Test collection > description"),
        publisher = Organization(
            orgPath = "/KOMMUNE/102117858",
            id = "Test publisher > identifier",
            uri = "Test publisher > uri ",
            name = "Test publisher > name",
            prefLabel = LocalizedStrings(
                "NB Test publisher > prefLabel",
                "NN Test publisher > prefLabel",
                "NO Test publisher > prefLabel",
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
