package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*

val TEST_NULL_EVENT = Event(
    uri = "null.com",
    harvest = null,
    catalog = null,
    title = null,
    description = null,
    subject = null,
)

val TEST_EVENT = TEST_NULL_EVENT.copy(
    title = LocalizedStrings(
        "NB Test prefLabel, title",
        "NN Test prefLabel",
        "EN Test prefLabel"),
    harvest =  HarvestMetadata(
        "2022-02-15T11:00:05Z",
        listOf("Test harvest > changed")),
    description = LocalizedStrings(
        "NB Test collection > description",
        "NN Test collection > description",
        "EN Test collection > description"),
)

val TEST_EVENT_HIT_ALL_FIELDS = TEST_NULL_EVENT.copy(
        uri = "uri 1061",
        title = LocalizedStrings(
                "NB title",
                "NN title",
                "EN title"),
        catalog = Catalog(
                description = LocalizedStrings(
                        "NB Test catalog > description",
                        "NN Test catalog > description",
                        "EN Test catalog > description"),
                id = "Test catalog > id 1061",
                uri = "Test catalog > uri 1061",
                title = LocalizedStrings(
                        "NB Test catalog > title",
                        "NN Test catalog > title",
                        "EN Test catalog > title"),
                publisher = Organization(
                        orgPath = "/PRIVAT/106117858",
                        identifier = "Test publisher > identifier",
                        uri = "Test publisher > uri",
                        name = "Test publisher > name",
                        prefLabel = LocalizedStrings(
                                "NB Test publisher > prefLabel",
                                "NN Test publisher > prefLabel",
                                "EN Test publisher > prefLabel"),
                ),
        ),
        description = LocalizedStrings(
                "NB Test description",
                "NN Test description",
                "EN Test description"),
        harvest =  HarvestMetadata(
            "2022-02-15T11:00:05Z",
                listOf("Test harvest > changed")),
)

val EVENT_WITH_RELATIONS = TEST_NULL_EVENT.copy(subject = listOf("subject_uri"))
