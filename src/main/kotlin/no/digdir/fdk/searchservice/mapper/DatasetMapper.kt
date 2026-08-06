package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.Dataset
import no.digdir.fdk.searchservice.model.MediaTypeOrExtentType
import no.digdir.fdk.searchservice.model.Reference
import no.digdir.fdk.searchservice.model.Relation
import no.digdir.fdk.searchservice.model.RelationType
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.SpecializedType

fun Dataset.toSearchObject(
    id: String,
    timestamp: Long,
    deleted: Boolean = false,
) = SearchObject(
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
    additionalTitles = null,
)

fun Dataset.extractPrefixedFormats(): Set<String> {
    val mutableList = mutableSetOf<String>()
    distribution?.forEach { dist ->
        dist.fdkFormat?.forEach { format ->
            if (format.type == MediaTypeOrExtentType.UNKNOWN) {
                mutableList.add(MediaTypeOrExtentType.UNKNOWN.name)
            } else {
                mutableList.add("${format.type} ${format.code}")
            }
        }
    }
    return mutableList
}

fun Dataset.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()

    conformsTo?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.CONFORMS_TO))
    }

    inSeries?.let {
        relations.add(Relation(uri = it.uri, type = RelationType.IN_SERIES))
    }

    references?.forEach {
        relations.add(Relation(uri = it.source?.uri, type = it.uriToRelationType() ?: RelationType.RELATION))
    }

    subject?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.SUBJECT))
    }

    return relations
}

private const val DC_TERMS_BASE_URI = "http://purl.org/dc/terms"

private fun Reference.uriToRelationType(): RelationType? =
    when (referenceType?.uri) {
        "$DC_TERMS_BASE_URI/source" -> RelationType.SOURCE
        "$DC_TERMS_BASE_URI/hasVersion" -> RelationType.HAS_VERSION
        "$DC_TERMS_BASE_URI/isVersionOf" -> RelationType.IS_VERSION_OF
        "$DC_TERMS_BASE_URI/isPartOf" -> RelationType.IS_PART_OF
        "$DC_TERMS_BASE_URI/hasPart" -> RelationType.HAS_PART
        "$DC_TERMS_BASE_URI/references" -> RelationType.REFERENCES
        "$DC_TERMS_BASE_URI/isReferencedBy" -> RelationType.IS_REFERENCED_BY
        "$DC_TERMS_BASE_URI/replaces" -> RelationType.REPLACES
        "$DC_TERMS_BASE_URI/isReplacedBy" -> RelationType.IS_REPLACED_BY
        "$DC_TERMS_BASE_URI/requires" -> RelationType.REQUIRES
        "$DC_TERMS_BASE_URI/isRequiredBy" -> RelationType.IS_REQUIRED_BY
        "$DC_TERMS_BASE_URI/relation" -> RelationType.RELATION
        else -> null
    }

fun Dataset.getSpecializedType(): SpecializedType? =
    when (specializedType) {
        "datasetSeries" -> SpecializedType.DATASET_SERIES
        else -> null
    }
