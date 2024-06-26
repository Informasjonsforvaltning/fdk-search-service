package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*

// ids start from 80 to avoid conflicts with the ids in the test data for the other types
val TEST_NULL_INFORMATION_MODEL = InformationModel(
    uri = "uri 80",
    title = null,
    catalog = null,
    description = null,
    keyword = null,
    theme = null,
    losTheme = null,
    publisher = null,
    accessRights = null,
    harvest = null,
    replaces = null,
    hasPart = null,
    isReplacedBy = null,
    isPartOf = null,
    subjects = null
)

val TEST_INFORMATION_MODEL_HIT_ALL_FIELDS = TEST_NULL_INFORMATION_MODEL.copy(
    uri = "uri 81",
    title = LocalizedStrings(
        "NB title",
        "NN title",
        "NO title",
        "EN title"),
    catalog = Catalog(
        description = LocalizedStrings(
            "NB Test catalog > description",
            "NN Test catalog > description",
            "NO Test catalog > description",
            "EN Test catalog > description"),
        id = "Test catalog > id",
        uri = "Test catalog > uri",
        title = LocalizedStrings(
            "NB Test catalog > title",
            "NN Test catalog > title",
            "NO Test catalog > title",
            "EN Test catalog > title"),
        publisher = Organization(
            orgPath = "/PRIVAT/108117858",
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
    keyword = listOf(
        LocalizedStrings(
            "NB Test keyword",
            "NN Test keyword",
            "NO Test keyword",
            "EN Test keyword")
    ),
    theme = listOf(
        EuDataTheme(
            title = LocalizedStrings(
                "NB Test theme > title",
                "NN Test theme > title",
                "NO Test theme > title",
                "EN Test theme > title"),
            code = "ENVI"),
        EuDataTheme(
            title = LocalizedStrings(
                "NB Test theme > title",
                "NN Test theme > title",
                "NO Test theme > title",
                "EN Test theme > title"),
            code = "REGI")
    ),
    losTheme = listOf(
        LosNode(
            name = LocalizedStrings(
                "NB Test losTheme > name",
                "NN Test losTheme > name",
                "NO Test losTheme > name",
                "EN Test losTheme > name"),
            losPaths = listOf("test"),
        ),
        LosNode(
            name = null,
            losPaths = listOf("trafikk-og-transport/mobilitetstilbud")
        )
    ),
    publisher = Organization(
        orgPath = "/STAT/108117858",
        id = "Test publisher > identifier",
        uri = "Test publisher > uri",
        name = "Test publisher > name",
        prefLabel = LocalizedStrings(
            "NB Test publisher > prefLabel",
            "NN Test publisher > prefLabel",
            "NO Test publisher > prefLabel",
            "EN Test publisher > prefLabel"),
    ),
    accessRights = ReferenceDataCode(
        uri = "Test accessRights > uri",
        code = "Test accessRights > code",
        prefLabel = LocalizedStrings(
            "NB Test accessRights > prefLabel",
            "NN Test accessRights > prefLabel",
            "NO Test accessRights > prefLabel",
            "EN Test accessRights > prefLabel"),
    ),
    harvest = HarvestMetadata(
        "2022-02-15T11:00:05Z",
        "2022-02-15T11:00:05Z"),
)

val INFORMATION_MODEL_WITH_RELATIONS = TEST_NULL_INFORMATION_MODEL.copy(
    replaces = "replaces_uri",
    hasPart = "hasPart_uri",
    isReplacedBy = "isReplacedBy_uri",
    isPartOf = "isPartOf_uri",
    subjects = listOf("subjects_uri")
)
