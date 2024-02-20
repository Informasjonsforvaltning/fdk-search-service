package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*


val TEST_NULL_SEARCH_OBJECT = SearchObject(
    id = "123456789",
    uri = "Test uri",
    accessRights = null,
    catalog = null,
    dataTheme = null,
    description = null,
    fdkFormatPrefixed = null,
    harvest = null,
    isOpenData = false,
    keyword = null,
    losTheme = null,
    organization = null,
    provenance = null,
    searchType = SearchType.DATASET,
    spatial = null,
    title = null
)

val TEST_SEARCH_OBJECT_AND_HIT_ALL_FIELDS = TEST_NULL_SEARCH_OBJECT.copy(
        accessRights = ReferenceDataCode("Test accessRights",
            code = "accessRights > code",
            prefLabel = LocalizedStrings(
                "NB Test accessRights > prefLabel",
                "NN Test accessRights > prefLabel",
                "EN Test accessRights > prefLabel")),
        catalog = Catalog(
            id = "123",
            uri = "catalog > uri",
            description = LocalizedStrings(
                "NB Test catalog > description",
                "NN Test catalog > description",
                "EN Test catalog > description"),
            publisher = Organization(
                orgPath = "/PRIVAT/972417858",
                identifier = "Test publisher > identifier",
                uri = "Test publisher > uri",
                name = "Test publisher > name",
                prefLabel = LocalizedStrings(
                    "NB Test publisher > prefLabel",
                    "NN Test publisher > prefLabel",
                    "EN Test publisher > prefLabel"),
            ),
            title = LocalizedStrings(
                "NB Test catalog > title",
                "NN Test catalog > title",
                "EN Test catalog > title")),
        dataTheme = listOf(
            EuDataTheme(
                title = LocalizedStrings(
                    "NB Test dataTheme",
                    "NN Test dataTheme",
                    "EN Test dataTheme"),
                code = "Test dataTheme > code")
        ),
        description = LocalizedStrings(
            "NB Test description",
            "NN Test description",
            "EN Test description"),
        fdkFormatPrefixed = listOf("Test fdkFormatPrefixed"),
        harvest = HarvestMetaData(
            firstHarvested = "Test harvest > firstHarvested",
            changed = listOf(
                "Test harvest > changed 1",
                "Test harvest > changed 2")),
        keyword = listOf(
            LocalizedStrings(
                "NB Test keyword",
                "NN Test keyword",
                "EN Test keyword")
        ),
        losTheme = listOf(
            LosNode(
                LocalizedStrings(
                    "NB Test losTheme > name 1",
                    "NN Test losTheme > name 1",
                    "EN Test losTheme > name 1")),
            LosNode(
                LocalizedStrings(
                    "NB Test losTheme > name 2",
                    "NN Test losTheme > name 2",
                    "EN Test losTheme > name 2"))
        ),
        organization = Organization(
            orgPath = "/STAT/072417858",
            identifier = "Test organization > identifier",
            uri = "Test publisher > uri",
            name = "Test publisher > name",
            prefLabel = LocalizedStrings(
                "NB Test publisher > prefLabel",
                "NN Test publisher > prefLabel",
                "EN Test publisher > prefLabel"),
        ),
        provenance = ReferenceDataCode(
            uri = "Test provenance > uri",
            code = "Test provenance > code",
            prefLabel = LocalizedStrings(
                "NB Test provenance > prefLabel",
                "NN Test provenance > prefLabel",
                "EN Test provenance > prefLabel")),
        spatial = listOf(
            ReferenceDataCode(
                uri = "Test spatial > uri",
                code = "Test spatial > code",
                prefLabel = LocalizedStrings(
                    "NB Test spatial > prefLabel",
                    "NN Test spatial > prefLabel",
                    "EN Test spatial > prefLabel"))
        ),
        title = LocalizedStrings(
            "NB Test title",
            "NN Test title",
            "EN Test title"),
)