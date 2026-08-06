package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

fun Dataset.toSearchObject(id: String, timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = uri,
        accessRights = accessRights,
        catalog = catalog,
        dataTheme = theme?.toSet(),
        description = description,
        fdkFormatPrefixed = extractPrefixedFormats(),
        metadata = harvest?.toMetadata(timestamp, deleted),
        isOpenData = isOpenData,
        keyword = keyword?.toSet(),
        losTheme = losTheme?.toSet(),
        organization = publisher,
        provenance = provenance,
        searchType = SearchType.DATASET,
        spatial = spatial?.toSet(),
        title = title,
        relations = getRelations(),
        specializedType = getSpecializedType(),
        isAuthoritative = isAuthoritative,
        isRelatedToTransportportal = isRelatedToTransportportal,
        additionalTitles = null
    )

fun Dataset.extractPrefixedFormats(): Set<String> {
    val mutableList = mutableSetOf<String>()
    distribution?.forEach { dist ->
        dist.fdkFormat?.forEach { format ->
            if(format.type == MediaTypeOrExtentType.UNKNOWN)
                mutableList.add(MediaTypeOrExtentType.UNKNOWN.name)
            else mutableList.add("${format.type} ${format.code}")
        }
    }
    return mutableList
}

fun Dataset.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()

    conformsTo?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.conformsTo))
    }

    inSeries?.let {
        relations.add(Relation(uri = it.uri, type = RelationType.inSeries))
    }

    references?.forEach {
        relations.add(Relation(uri = it.source?.uri, type = it.uriToRelationType() ?: RelationType.relation))
    }

    subject?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.subject))
    }

    return relations
}

private const val DC_TERMS_BASE_URI = "http://purl.org/dc/terms"

private fun Reference.uriToRelationType(): RelationType? =
    when (referenceType?.uri) {
        "$DC_TERMS_BASE_URI/source" -> RelationType.source
        "$DC_TERMS_BASE_URI/hasVersion" -> RelationType.hasVersion
        "$DC_TERMS_BASE_URI/isVersionOf" -> RelationType.isVersionOf
        "$DC_TERMS_BASE_URI/isPartOf" -> RelationType.isPartOf
        "$DC_TERMS_BASE_URI/hasPart" -> RelationType.hasPart
        "$DC_TERMS_BASE_URI/references" -> RelationType.references
        "$DC_TERMS_BASE_URI/isReferencedBy" -> RelationType.isReferencedBy
        "$DC_TERMS_BASE_URI/replaces" -> RelationType.replaces
        "$DC_TERMS_BASE_URI/isReplacedBy" -> RelationType.isReplacedBy
        "$DC_TERMS_BASE_URI/requires" -> RelationType.requires
        "$DC_TERMS_BASE_URI/isRequiredBy" -> RelationType.isRequiredBy
        "$DC_TERMS_BASE_URI/relation" -> RelationType.relation
        else -> null
    }

fun Dataset.getSpecializedType(): SpecializedType? {
    return when (specializedType) {
        "datasetSeries" -> SpecializedType.DATASET_SERIES
        else -> null
    }
}
