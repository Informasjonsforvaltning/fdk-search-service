package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*

val TEST_NULL_SERVICE = Service(
    uri = "null.com",
    harvest = null,
    losTheme = null,
    catalog = null,
    title = null,
    keyword = null,
    description = null,
    spatial = null,
    hasCompetentAuthority = null,
    ownedBy = null,
    euDataThemes = null,
    subject = null,
    relation = null,
    requires = null,
    isDescribedAt = null,
    isGroupedBy = null,
    isClassifiedBy = null
)

val TEST_SERVICE = TEST_NULL_SERVICE.copy(
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

val TEST_SERVICE_HIT_ALL_FIELDS = TEST_NULL_SERVICE.copy(
    uri = "uri 0101",
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
        id = "Test catalog > id 101",
        uri = "Test catalog > uri 101",
        title = LocalizedStrings(
            "NB Test catalog > title",
            "NN Test catalog > title",
            "NO Test catalog > title",
            "EN Test catalog > title"),
        publisher = Organization(
            orgPath = "/PRIVAT/101417858",
            id = "Test publisher > identifier 101",
            uri = "Test publisher > uri 101",
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
    euDataThemes = listOf(
        EuDataTheme(
            title = LocalizedStrings(
                "NB Test euDataThemes > title",
                "NN Test euDataThemes > title",
                "NO Test euDataThemes > title",
                "EN Test euDataThemes > title"),
            code = "ENVI"),
        EuDataTheme(
            title = LocalizedStrings(
                "NB Test euDataThemes > title",
                "NN Test euDataThemes > title",
                "NO Test euDataThemes > title",
                "EN Test euDataThemes > title"),
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
        )
    ),
    spatial = listOf("Test spatial > uri"),
    harvest = HarvestMetadata(
        "2022-02-15T11:00:05Z",
        "2022-02-15T11:00:05Z"),
)

val TEST_SERVICE_HIT_OWNED_BY = TEST_SERVICE_HIT_ALL_FIELDS.copy(
    uri = "uri 0102",
    keyword = listOf(LocalizedStrings(nb = "keyword", nn = "keyword", no = "keyword", en = "keyword")),
    ownedBy = listOf(ServiceOrganization(
        orgPath = "/STAT/010247858",
        identifier = "test identifier 0102",
        uri = "test uri 0102",
        name = LocalizedStrings("test name 0102", null, null, null),
        title = LocalizedStrings(
            "NB Test ownedBy > prefLabel",
            "NN Test ownedBy > prefLabel",
            "NO Test ownedBy > prefLabel",
            "EN Test ownedBy > prefLabel"),
    ))
)

val TEST_SERVICE_HIT_HAS_COMPETENT_AUTHORITY = TEST_NULL_SERVICE.copy(
    uri = "uri 0103",
    hasCompetentAuthority = listOf(ServiceOrganization(
        orgPath = "/STAT/103417858",
        identifier = "test identifier 0103",
        uri = "test uri 0103",
        name = LocalizedStrings("test name 0103", null, null, null),
        title = LocalizedStrings(
            "NB Test hasCompetentAuthority > prefLabel",
            "NN Test hasCompetentAuthority > prefLabel",
            "NO Test hasCompetentAuthority > prefLabel",
            "EN Test hasCompetentAuthority > prefLabel"),
    ))
)

val SERVICE_WITH_RELATIONS = TEST_NULL_SERVICE.copy(
    subject = listOf(ObjectWithURI(uri = "subject_uri")),
    relation = listOf(ObjectWithURI(uri = "relation_uri")),
    requires = listOf(ObjectWithURI(uri = "requires_uri")),
    isDescribedAt = listOf(ObjectWithURI(uri = "isDescribedAt_uri")),
    isGroupedBy = listOf("isGroupedBy_uri"),
    isClassifiedBy = listOf(ObjectWithURI(uri = "isClassifiedBy_uri")),
)
