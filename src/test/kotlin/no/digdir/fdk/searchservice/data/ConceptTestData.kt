package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*
import no.digdir.fdk.searchservice.model.Collection

val TEST_NULL_CONCEPT = Concept(
    id = "0",
    identifier = "identifier 0",
    publisher = null,
    definition = null,
    prefLabel = null,
    harvest = null,
    collection = null)

val TEST_CONCEPT_HIT_ALL_FIELDS = TEST_NULL_CONCEPT.copy(
    id = "1",
    identifier = "identifier 1",
    publisher = Organization(
        orgPath = "/STAT/972417858",
        identifier = "Test publisher > identifier",
        uri = "Test publisher > uri",
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
        "NB Test prefLabel",
        "NN Test prefLabel",
        "EN Test prefLabel"),
    harvest = HarvestMetaData(
        "Test harvest > firstHarvested",
        listOf("Test harvest > changed")),
    collection = Collection(
        id = "Test collection > id",
        uri = "Test collection > uri",
        label = LocalizedStrings(
            "NB Test collection > label",
            "NN Test collection > label",
            "EN Test collection > label"),
        description = LocalizedStrings(
            "NB Test collection > description",
            "NN Test collection > description",
            "EN Test collection > description"),
        publisher = Organization(
            orgPath = "/KOMMUNE/972417858",
            identifier = "Test publisher > identifier",
            uri = "Test publisher > uri",
            name = "Test publisher > name",
            prefLabel = LocalizedStrings(
                "NB Test publisher > prefLabel",
                "NN Test publisher > prefLabel",
            "EN Test publisher > prefLabel"))
    )
)
