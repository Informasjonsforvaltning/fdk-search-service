package no.digdir.fdk.searchservice.data

import no.digdir.fdk.searchservice.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val TEST_NULL_DATASET = Dataset(
    uri = "test uri",
    title = null,
    description = null,
    keyword = null,
    theme = null,
    losTheme = null,
    publisher = null,
    accessRights = null,
    isOpenData = false,
    spatial = null,
    provenance = null,
    harvest = null,
    catalog = null,
    distribution = null,
    subject = null,
    conformsTo = null,
    inSeries = null,
    references = null,
    specializedType = null,
    isAuthoritative = null,
    isRelatedToTransportportal = null
)

val TEST_DATASET_HIT_ALL_FIELDS = TEST_NULL_DATASET.copy(
    title = LocalizedStrings(
        "NB Test title",
        "NN Test title",
        "NO Test title",
        "EN Test title"),
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
            losPaths = listOf("familie-og-barn"),
        ),
        LosNode(
            name = null,
            losPaths = listOf("trafikk-og-transport/mobilitetstilbud")
        )
    ),
    isRelatedToTransportportal = true,
    publisher = Organization(
        orgPath = "/STAT/272417858",
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
        uri = "accessRights > uri",
        code = "PUBLIC",
        prefLabel = LocalizedStrings(
            "NB Test accessRights > prefLabel",
            "NN Test accessRights > prefLabel",
            "NO Test accessRights > prefLabel",
            "EN Test accessRights > prefLabel")
    ),
    spatial = listOf(
        ReferenceDataCode(
            uri = "spatial > uri",
            code = "Test spatial > code",
            prefLabel = LocalizedStrings(
                "NB Test spatial > prefLabel",
                "NN Test spatial > prefLabel",
                "NO Test spatial > prefLabel",
                "EN Test spatial > prefLabel")
        )
    ),
    provenance = ReferenceDataCode(
        uri = "provenance > uri",
        code = "Test provenance > code",
        prefLabel = LocalizedStrings(
            "NB Test provenance > prefLabel",
            "NN Test provenance > prefLabel",
            "NO Test provenance > prefLabel",
            "EN Test provenance > prefLabel")
    ),
    harvest = HarvestMetadata(
        firstHarvested = LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE),
        modified = LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE)
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
        ))
)

val TEST_DATASET_FILTERS = TEST_DATASET_HIT_ALL_FIELDS.copy(
    isOpenData = true,
    uri = "dataset.id3.uri",
    provenance = ReferenceDataCode(
        uri = "provenance > uri",
        code = "BRUKER",
        prefLabel = LocalizedStrings(
            "Bruker",
            "Brukar",
            "Bruker",
            "User")
    ),
    spatial = listOf(
        ReferenceDataCode(
            uri = "spatial > uri",
            code = "123789",
            prefLabel = LocalizedStrings(
                "Norge",
                "Noreg",
                "Norge",
                "Norway")
        ),
        ReferenceDataCode(
            uri = "spatial > uri",
            code = "123456",
            prefLabel = LocalizedStrings(nb = "Spania", null, null, null)
        ),
        ReferenceDataCode(
            uri = "spatial > uri",
            code = "56789",
            prefLabel = LocalizedStrings(nb = "Sogn og fjordane", null, null, null)
        )
    ),
    losTheme = listOf(
        LosNode(
            name = null,
            losPaths = listOf("familie-og-barn"),
        ),
        LosNode(
            name = null,
            losPaths = listOf("demokrati-og-innbyggerrettigheter/politikk-og-valg"),
        ),
    ),
    publisher = Organization(
        orgPath = "/FYLKE",
        id = "Test publisher > identifier",
        uri = "Test publisher > uri",
        name = "Test publisher > name",
        prefLabel = null
    ),
    distribution = listOf(
        Distribution(
            listOf(
                MediaTypeOrExtent(name = null, uri = null, type = MediaTypeOrExtentType.MEDIA_TYPE, code = "tiff"),
                MediaTypeOrExtent(name = null, uri = null, type = MediaTypeOrExtentType.FILE_TYPE, code = "SHP"),
                MediaTypeOrExtent(name = null, uri = null, type = MediaTypeOrExtentType.UNKNOWN, code = "LOREM")
            ))
    )
)

val TEST_DATASET_HIT_IS_OPEN = TEST_DATASET_HIT_ALL_FIELDS.copy(
    uri = "dataset.uri.2",
    isOpenData = true,
    conformsTo = listOf(ObjectWithURI(uri = TEST_DATASET_FILTERS.uri))
)

val basePath = "http://purl.org/dc/terms"
val DATASET_WITH_RELATIONS = TEST_NULL_DATASET.copy(
    subject = listOf(ObjectWithURI(uri = "subject_uri")),
    conformsTo = listOf(ObjectWithURI(uri = "conformsTo_uri")),
    inSeries = ObjectWithURI(uri = "inSeries_uri"),
    references = listOf(
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/source"), source = (ObjectWithURI(uri = "source_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/hasVersion"), source = (ObjectWithURI(uri = "has_version_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/isVersionOf"), source = (ObjectWithURI(uri = "is_version_of_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/isPartOf"), source = (ObjectWithURI(uri = "is_part_of_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/hasPart"), source = (ObjectWithURI(uri = "has_part_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/references"), source = (ObjectWithURI(uri = "references_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/isReferencedBy"), source = (ObjectWithURI(uri = "is_referenced_by_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/replaces"), source = (ObjectWithURI(uri = "replaces_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/isReplacedBy"), source = (ObjectWithURI(uri = "is_replaced_by_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/requires"), source = (ObjectWithURI(uri = "requires_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/isRequiredBy"), source = (ObjectWithURI(uri = "is_required_by_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = "$basePath/relation"), source = (ObjectWithURI(uri = "relation_uri"))),
        Reference(referenceType = ReferenceDataCode(code = null, prefLabel = null, uri = null), source = (ObjectWithURI(uri = "missing_type_uri")))
    )
)

