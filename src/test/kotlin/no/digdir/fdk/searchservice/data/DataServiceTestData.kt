package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*

val TEST_NULL_DATA_SERVICE = DataService(
    id = "0",
    uri = "null.com",
    publisher = null,
    harvest = null,
    accessRights = null,
    theme = null,
    losTheme = null,
    catalog = null,
    title = null,
    keyword = null,
    description = null,
)

val TEST_DATA_SERVICE = TEST_NULL_DATA_SERVICE.copy(
    id = "456",
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