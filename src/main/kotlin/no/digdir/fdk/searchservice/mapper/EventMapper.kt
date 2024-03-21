package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

fun Event.toSearchObject(id: String, timestamp: Long, deleted: Boolean = false) =
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
        relations = getRelations(),
        specializedType = getSpecializedType(),
    )

fun Event.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()


    subject?.forEach {
        relations.add(Relation(uri = it, type = RelationType.subject))
    }

    return relations
}

fun Event.getSpecializedType(): SpecializedType? {
    return when (specializedType) {
        "life_event" -> SpecializedType.LIFE_EVENT
        "business_event" -> SpecializedType.BUSINESS_EVENT
        else -> null
    }
}
