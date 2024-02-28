package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.DataService
import no.digdir.fdk.searchservice.model.Relation
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.*

fun DataService.toSearchObject(timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = uri,
        accessRights = accessRights,
        catalog = catalog,
        dataTheme = theme,
        description = description,
        fdkFormatPrefixed = extractPrefixedFormats(),
        metadata = harvest?.toMetadata(timestamp, deleted),
        isOpenData = null,
        keyword = keyword,
        losTheme = losTheme,
        organization = publisher,
        provenance = null,
        searchType = SearchType.DATA_SERVICE,
        spatial = null,
        title = title,
        relations = getRelations()
    )

fun DataService.extractPrefixedFormats(): List<String> {
    val mutableList = mutableListOf<String>()
        fdkFormat?.forEach { format ->
            mutableList.add("${format.type} ${format.code}")
        }
    return mutableList
}

private fun DataService.getRelations(): List<Relation> {
    val relations: MutableList<Relation> = mutableListOf()

    conformsTo?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.conformsTo))
    }

    servesDataset?.forEach {
        relations.add(Relation(uri = it, type = RelationType.servesDataset))
    }

    return relations
}
