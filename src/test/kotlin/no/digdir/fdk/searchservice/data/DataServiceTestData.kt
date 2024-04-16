package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*

val TEST_NULL_DATA_SERVICE = DataService(
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
    servesDataset = null,
    conformsTo = null,
    fdkFormat = null
)

val TEST_DATA_SERVICE_HIT_ALL_FIELDS = TEST_NULL_DATA_SERVICE.copy(
    uri = "http://localhost:5000/catalogs/456",
    publisher = Organization(
        orgPath = "/STAT/972417858",
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
        uri = "http://publications.europa.eu/resource/authority/access-right/PUBLIC",
        code = "PUBLIC",
        prefLabel = LocalizedStrings(
            "accessRights > NB Public",
            "accessRights> NN Public",
            "accessRights > NO Public",
            "accessRights > EN Public"),
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
            losPaths = listOf("familie-og-barn"),
        )
    ),
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
            orgPath = "/PRIVAT/172417858",
            id = "Test publisher > identifier",
            uri = "Test publisher > uri",
            name = "Test publisher > name",
            prefLabel = LocalizedStrings(
                "NB Test publisher > prefLabel",
                "NN Test publisher > prefLabel",
                "NO Test publisher > prefLabel",
                "EN Test publisher > prefLabel"),
        )),
    title = LocalizedStrings(
        "NB Test prefLabel, title",
        "NN Test prefLabel",
        "NO Test prefLabel, title",
        "EN Test prefLabel"),
    harvest = HarvestMetadata(
        "2022-02-15T11:00:05Z",
        "2023-02-15T11:00:05Z"),
    description = LocalizedStrings(
        "NB Test collection > description",
        "NN Test collection > description",
        "NO Test collection > description",
        "EN Test collection > description"),
    keyword = listOf(
        LocalizedStrings(
            "NB Test keyword > prefLabel",
            "NN Test keyword > prefLabel",
            "NO Test keyword > prefLabel",
            "EN Test keyword > prefLabel"),
        LocalizedStrings(
            "NB Test keyword > prefLabel",
            "NN Test keyword > prefLabel",
            "NO Test keyword > prefLabel",
            "EN Test keyword > prefLabel")
    ),
    fdkFormat = listOf(
        MediaTypeOrExtent(name = null, uri = null, type = MediaTypeOrExtentType.MEDIA_TYPE, code = "turtle"),
        MediaTypeOrExtent(name = null, uri = null, type = MediaTypeOrExtentType.UNKNOWN, code = "IPSUM")
    )
)

val DATASERVICE_WITH_RELATIONS = TEST_NULL_DATA_SERVICE.copy(
    servesDataset = listOf("servesDataset_uri"),
    conformsTo = listOf(ObjectWithURI(uri = "conformsTo_uri"))
)
