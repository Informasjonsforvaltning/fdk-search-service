package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*

val TEST_NULL_EVENT = Event(
    uri = "null.com",
    harvest = null,
    catalog = null,
    title = null,
    description = null,
    subject = null,
    specializedType = null
)

val TEST_EVENT = TEST_NULL_EVENT.copy(
    title = LocalizedStrings(
        "NB Test prefLabel, title",
        "NN Test prefLabel",
        "NO Test prefLabel, title",
        "EN Test prefLabel"),
    harvest = HarvestMetadata(
        "2022-02-15T11:00:05Z",
        "2022-02-15T11:00:05Z"),
    description = LocalizedStrings(
        "NB Test collection > description",
        "NN Test collection > description",
        "NO Test collection > description",
        "EN Test collection > description"),
)

val TEST_EVENT_HIT_ALL_FIELDS = TEST_NULL_EVENT.copy(
    uri = "uri 1061",
    title = LocalizedStrings(
        "NB title 1061",
        "NN title 1061",
        "NO title 1061",
        "EN title 1061"),
    catalog = Catalog(
        description = LocalizedStrings(
            "NB Test catalog > description",
            "NN Test catalog > description",
            "NO Test catalog > description",
            "EN Test catalog > description"),
        id = "Test catalog > id 1061",
        uri = "Test catalog > uri 1061",
        title = LocalizedStrings(
            "NB Test catalog > title",
            "NN Test catalog > title",
            "NO Test catalog > title",
            "EN Test catalog > title"),
        publisher = Organization(
            orgPath = "/PRIVAT/111222333/333222111",
            id = "Test publisher > identifier",
            uri = "Test publisher > uri",
            name = "Test publisher > name",
            prefLabel = LocalizedStrings(
                "NB Test publisher > prefLabel",
                "NN Test publisher > prefLabel",
                "NO Test publisher > prefLabel",
                "EN Test publisher > prefLabel"),
        ),
    ),
    description = LocalizedStrings(
        "NB Test description",
        "NN Test description",
        "NO Test description",
        "EN Test description"),
    harvest = HarvestMetadata(
        "2022-02-15T11:00:05Z",
        "2022-02-15T11:00:05Z"),
)

val EVENT_WITH_RELATIONS = TEST_NULL_EVENT.copy(subject = listOf("subject_uri"))
