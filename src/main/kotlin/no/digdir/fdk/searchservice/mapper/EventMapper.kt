package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.Event
import no.digdir.fdk.searchservice.model.Relation
import no.digdir.fdk.searchservice.model.RelationType
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.SpecializedType

fun Event.toSearchObject(
    id: String,
    timestamp: Long,
    deleted: Boolean = false,
) = SearchObject(
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
    organization = catalog?.publisher,
    provenance = null,
    searchType = SearchType.EVENT,
    spatial = null,
    title = title,
    relations = getRelations(),
    specializedType = getSpecializedType(),
    isAuthoritative = null,
    isRelatedToTransportportal = false,
    additionalTitles = null,
)

fun Event.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()

    subject?.forEach {
        relations.add(Relation(uri = it, type = RelationType.SUBJECT))
    }

    return relations
}

fun Event.getSpecializedType(): SpecializedType? =
    when (specializedType) {
        "life_event" -> SpecializedType.LIFE_EVENT
        "business_event" -> SpecializedType.BUSINESS_EVENT
        else -> null
    }
