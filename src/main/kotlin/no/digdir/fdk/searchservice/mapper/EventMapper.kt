package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.Event
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType

fun Event.toSearchObject(timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = uri,
        accessRights = null,
        catalog = catalog,
        dataTheme = null,
        description = description,
        fdkFormatPrefixed = null,
        metadata = harvest?.toMetadata(timestamp, deleted),
        isOpenData = null,
        keyword = null,
        losTheme = null,
        organization = null,
        provenance = null,
        searchType = SearchType.EVENT,
        spatial = null,
        title = title,
        relations = getRelations()
    )

private fun Event.getRelations(): List<Relation> {
    val relations: MutableList<Relation> = mutableListOf()

    mayTrigger?.forEach {
        relations.add(Relation(uri = it, type = RelationType.mayTrigger))
    }

    subject?.forEach {
        relations.add(Relation(uri = it, type = RelationType.subject))
    }

    return relations
}