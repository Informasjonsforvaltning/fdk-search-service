package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.InformationModel
import no.digdir.fdk.searchservice.model.Relation
import no.digdir.fdk.searchservice.model.RelationType
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType

fun InformationModel.toSearchObject(
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
    fdkFormatPrefixed = null,
    metadata = harvest?.toMetadata(timestamp, deleted),
    isOpenData = null,
    keyword = keyword?.toSet(),
    losTheme = losTheme?.toSet(),
    organization = publisher,
    provenance = null,
    searchType = SearchType.INFORMATION_MODEL,
    spatial = null,
    title = title,
    relations = getRelations(),
    specializedType = null,
    isAuthoritative = null,
    isRelatedToTransportportal = false,
    additionalTitles = null,
)

fun InformationModel.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()

    hasPart?.let {
        relations.add(Relation(uri = hasPart, type = RelationType.HAS_PART))
    }

    isPartOf?.let {
        relations.add(Relation(uri = isPartOf, type = RelationType.IS_PART_OF))
    }

    isReplacedBy?.let {
        relations.add(Relation(uri = isReplacedBy, type = RelationType.IS_REPLACED_BY))
    }

    replaces?.let {
        relations.add(Relation(uri = it, type = RelationType.REPLACES))
    }

    subjects?.forEach {
        relations.add(Relation(uri = it, type = RelationType.SUBJECT))
    }

    return relations
}
