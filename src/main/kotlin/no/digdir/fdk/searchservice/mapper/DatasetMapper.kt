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
        relations.add(Relation(uri = it.uri, type = "conformsTo"))
    }

    inSeries?.forEach {
        relations.add(Relation(uri = it.uri, type = "inSeries"))
    }

    informationModel?.forEach {
        relations.add(Relation(uri = it.uri, type = "informationModel"))
    }

    references?.forEach {
        relations.add(Relation(uri = it.source, type = it.referenceType?.uri ?: "references"))
    }

    subject?.forEach {
        relations.add(Relation(uri = it.uri, type = "subject"))
    }

    return relations
}