package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*

val TEST_NULL_EVENT = Event(
    id = "0",
    uri = "null.com",
    harvest = null,
    catalog = null,
    title = null,
    description = null
)

val TEST_EVENT = TEST_NULL_EVENT.copy(
    id = "111",
    title = LocalizedStrings(
        "NB Test prefLabel, title",
        "NN Test prefLabel",
        "EN Test prefLabel"),
    harvest =  HarvestMetadata(
        "Test harvest > firstHarvested",
        listOf("Test harvest > changed")),
    description = LocalizedStrings(
        "NB Test collection > description",
        "NN Test collection > description",
        "EN Test collection > description"),
)