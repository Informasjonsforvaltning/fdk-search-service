package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

fun InformationModel.toSearchObject(id: String, timestamp: Long, deleted: Boolean = false) =
    SearchObject(
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
        isAuthoritative = null
    )

fun InformationModel.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()


    hasPart?.let {
        relations.add(Relation(uri = hasPart, type = RelationType.hasPart))
    }

    isPartOf?.let {
        relations.add(Relation(uri = isPartOf, type = RelationType.isPartOf))
    }

    isReplacedBy?.let {
        relations.add(Relation(uri = isReplacedBy, type = RelationType.isReplacedBy))
    }

    replaces?.let {
        relations.add(Relation(uri = it, type = RelationType.replaces))
    }


    subjects?.forEach {
        relations.add(Relation(uri = it, type = RelationType.subject))
    }

    return relations
}
