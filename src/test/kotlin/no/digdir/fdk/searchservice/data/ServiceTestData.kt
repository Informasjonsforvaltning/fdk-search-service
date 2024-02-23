package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*

val TEST_NULL_SERVICE = Service(
    id = "0",
    uri = "null.com",
    harvest = null,
    losTheme = null,
    catalog = null,
    title = null,
    keyword = null,
    description = null,
    spatial = null,
    hasCompetantAuthority = null,
    ownedBy = null,
    euDataThemes = null
)

val TEST_SERVICE = TEST_NULL_SERVICE.copy(
    id = "678",
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