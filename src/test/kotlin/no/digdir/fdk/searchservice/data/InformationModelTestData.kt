package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*

// ids start from 80 to avoid conflicts with the ids in the test data for the other types
val TEST_NULL_INFORMATION_MODEL = InformationModel(
    id = "1080",
    uri = "uri 80",
    title = null,
    catalog = null,
    description = null,
    keyword = null,
    theme = null,
    losTheme = null,
    publisher = null,
    accessRights = null,
    harvest = null
)

val TEST_INFORMATION_MODEL_HIT_ALL_FIELDS = TEST_NULL_INFORMATION_MODEL.copy(
    id = "1081",
    uri = "uri 81",
    title = LocalizedStrings(
        "NB title",
        "NN title",
        "EN title"),
    catalog = Catalog(
        description = LocalizedStrings(
            "NB Test catalog > description",
            "NN Test catalog > description",
            "EN Test catalog > description"),
        id = "Test catalog > id",
        uri = "Test catalog > uri",
        title = LocalizedStrings(
            "NB Test catalog > title",
            "NN Test catalog > title",
            "EN Test catalog > title"),
        publisher = Organization(
            orgPath = "/PRIVAT/108117858",
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
    keyword = listOf(
        LocalizedStrings(
            "NB Test keyword",
            "NN Test keyword",
            "EN Test keyword")
    ),
    theme = listOf(
        EuDataTheme(
            title = LocalizedStrings(
                "NB Test theme > title",
                "NN Test theme > title",
                "EN Test theme > title"),
            code = "ENVI"),
        EuDataTheme(
            title = LocalizedStrings(
                "NB Test theme > title",
                "NN Test theme > title",
                "EN Test theme > title"),
            code = "REGI")
    ),
    losTheme = listOf(
        LosNode(
            name = LocalizedStrings(
                "NB Test losTheme > name",
                "NN Test losTheme > name",
                "EN Test losTheme > name"),
            losPaths = "test",
        )
    ),
    publisher = Organization(
        orgPath = "/STAT/108117858",
        identifier = "Test publisher > identifier",
        uri = "Test publisher > uri",
        name = "Test publisher > name",
        prefLabel = LocalizedStrings(
            "NB Test publisher > prefLabel",
            "NN Test publisher > prefLabel",
            "EN Test publisher > prefLabel"),
    ),
    accessRights = ReferenceDataCode(
        uri = "Test accessRights > uri",
        code = "Test accessRights > code",
        prefLabel = LocalizedStrings(
            "NB Test accessRights > prefLabel",
            "NN Test accessRights > prefLabel",
            "EN Test accessRights > prefLabel"),
    ),
    harvest =  HarvestMetadata(
            "Test harvest > firstHarvested",
            listOf("Test harvest > changed")),
)
