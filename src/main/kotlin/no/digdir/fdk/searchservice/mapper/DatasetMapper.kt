package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

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

fun Dataset.getRelations(): List<Relation> {
    val relations: MutableList<Relation> = mutableListOf()

    conformsTo?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.conformsTo))
    }

    inSeries?.let {
        relations.add(Relation(uri = it.uri, type = RelationType.inSeries))
    }

    informationModel?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.informationModel))
    }

    references?.forEach {
        relations.add(Relation(uri = it.source?.uri, type = it.uriToRelationType() ?: RelationType.relation))
    }

    subject?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.subject))
    }

    return relations
}

private fun Reference.uriToRelationType(): RelationType? {
    val basePath = "http://purl.org/dc/terms";
    return when (referenceType?.uri) {
        "$basePath/source" -> RelationType.source
        "$basePath/hasVersion" -> RelationType.hasVersion
        "$basePath/isVersionOf" -> RelationType.isVersionOf
        "$basePath/isPartOf" -> RelationType.isPartOf
        "$basePath/hasPart" -> RelationType.hasPart
        "$basePath/references" -> RelationType.references
        "$basePath/isReferencedBy" -> RelationType.isReferencedBy
        "$basePath/replaces" -> RelationType.replaces
        "$basePath/isReplacedBy" -> RelationType.isReplacedBy
        "$basePath/requires" -> RelationType.requires
        "$basePath/isRequiredBy" -> RelationType.isRequiredBy
        "$basePath/relation" -> RelationType.relation
        else -> null
    }
}
