package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.Dataset
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType

fun Dataset.toSearchObject(timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = uri,
        accessRights = accessRights,
        catalog = catalog,
        dataTheme = theme,
        description = description,
        fdkFormatPrefixed = extractPrefixedFormats(),
        metadata = harvest?.toMetadata(timestamp, deleted),
        isOpenData = isOpenData,
        keyword = keyword,
        losTheme = losTheme,
        organization = publisher,
        provenance = provenance,
        searchType = SearchType.DATASET,
        spatial = spatial,
        title = title,
        relations = getRelations()
    )

fun Dataset.extractPrefixedFormats(): List<String> {
    val mutableList = mutableListOf<String>()
    distribution?.forEach { dist ->
        dist.fdkFormat?.forEach { format ->
            mutableList.add("${format.type} ${format.code}")
        }
    }
    return mutableList
}

private fun Dataset.getRelations(): List<Relation> {
    val relations: MutableList<Relation> = mutableListOf()

    conformsTo?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.conformsTo))
    }

    inSeries?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.inSeries))
    }

    informationModel?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.informationModel))
    }

    references?.forEach {
        relations.add(Relation(uri = it.source?.uri, type = it.uriToRelationType() ?: RelationType.references))
    }

    subject?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.subject))
    }

    return relations
}

private fun Reference.uriToRelationType(): RelationType? {
    return when (referenceType?.uri) {
        "http://purl.org/dc/terms/source" -> RelationType.source
        "http://purl.org/dc/terms/hasVersion" -> RelationType.hasVersion
        "http://purl.org/dc/terms/isVersionOf" -> RelationType.isVersionOf
        "http://purl.org/dc/terms/isPartOf" -> RelationType.isPartOf
        "http://purl.org/dc/terms/hasPart" -> RelationType.hasPart
        "http://purl.org/dc/terms/references" -> RelationType.references
        "http://purl.org/dc/terms/isReferencedBy" -> RelationType.isReferencedBy
        "http://purl.org/dc/terms/replaces" -> RelationType.replaces
        "http://purl.org/dc/terms/isReplacedBy" -> RelationType.isReplacedBy
        "http://purl.org/dc/terms/requires" -> RelationType.requires
        "http://purl.org/dc/terms/isRequiredBy" -> RelationType.isRequiredBy
        "http://purl.org/dc/terms/relation" -> RelationType.relation
        else -> null
    }
}
