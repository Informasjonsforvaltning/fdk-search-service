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
    euDataThemes = null,
    subject = null,
    relation = null,
    requires = null,
    isDescribedAt = null,
    isGroupedBy = null,
    isClassifiedBy = null
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

val TEST_SERVICE_HIT_ALL_FIELDS = TEST_NULL_SERVICE.copy(
        id = "0101",
        uri = "uri 0101",
        title = LocalizedStrings(
                "NB title",
                "NN title",
                "EN title"),
        catalog = Catalog(
                description = LocalizedStrings(
                        "NB Test catalog > description",
                        "NN Test catalog > description",
                        "EN Test catalog > description"),
                id = "Test catalog > id 101",
                uri = "Test catalog > uri 101",
                title = LocalizedStrings(
                        "NB Test catalog > title",
                        "NN Test catalog > title",
                        "EN Test catalog > title"),
                publisher = Organization(
                        orgPath = "/PRIVAT/101417858",
                        identifier = "Test publisher > identifier 101",
                        uri = "Test publisher > uri 101",
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
        euDataThemes = listOf(
                EuDataTheme(
                        title = LocalizedStrings(
                                "NB Test euDataThemes > title",
                                "NN Test euDataThemes > title",
                                "EN Test euDataThemes > title"),
                        code = "ENVI"),
                EuDataTheme(
                        title = LocalizedStrings(
                                "NB Test euDataThemes > title",
                                "NN Test euDataThemes > title",
                                "EN Test euDataThemes > title"),
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
        spatial = listOf(
                ReferenceDataCode(
                        uri = "Test spatial > uri",
                        code = "Test spatial > code",
                        prefLabel = LocalizedStrings(
                                "NB Test spatial > prefLabel",
                                "NN Test spatial > prefLabel",
                                "EN Test spatial > prefLabel"),
                )
        ),
        harvest =  HarvestMetadata(
                "Test harvest > firstHarvested",
                listOf("Test harvest > changed")),
)

val TEST_SERVICE_HIT_OWNED_BY = TEST_SERVICE_HIT_ALL_FIELDS.copy(
        id = "0102",
        uri = "uri 0102",
        ownedBy = listOf( Organization(
                orgPath = "/STAT/010247858",
                identifier = "test identifier 0102",
                uri = "test uri 0102",
                name = "test name 0102",
                prefLabel = LocalizedStrings(
                        "NB Test ownedBy > prefLabel",
                        "NN Test ownedBy > prefLabel",
                        "EN Test ownedBy > prefLabel"),
        ))
)

val TEST_SERVICE_HIT_HAS_COMPETANT_AUTHORITY =  TEST_NULL_SERVICE.copy(
        id = "0103",
        uri = "uri 0103",
        hasCompetantAuthority = listOf( Organization(
                orgPath = "/STAT/103417858",
                identifier = "test identifier 0103",
                uri = "test uri 0103",
                name = "test name 0103",
                prefLabel = LocalizedStrings(
                        "NB Test hasCompetantAuthority > prefLabel",
                        "NN Test hasCompetantAuthority > prefLabel",
                        "EN Test hasCompetantAuthority > prefLabel"),
        ))
)
