package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

fun Service.toSearchObject(timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = uri,
        accessRights = null,
        catalog = catalog,
        dataTheme = euDataThemes,
        description = description,
        fdkFormatPrefixed = null,
        metadata = harvest?.toMetadata(timestamp, deleted),
        isOpenData = null,
        keyword = keyword,
        losTheme = losTheme,
        organization = getOrganization(),
        provenance = null,
        searchType = SearchType.SERVICE,
        spatial = spatial,
        title = title,
        relations = getRelations()
    )

private fun Service.getOrganization() = if (hasCompetantAuthority.isNullOrEmpty()) {
    ownedBy?.get(0)
} else {
    hasCompetantAuthority.get(0)
}


private fun Service.getRelations(): List<Relation> {
    val relations: MutableList<Relation> = mutableListOf()

    isGroupedBy?.forEach {
        relations.add(Relation(uri = it, type = RelationType.isGroupedBy))
    }

    isClassifiedBy?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.isClassifiedBy))
    }

    isDescribedAt?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.isDescribedAt))
    }

    relation?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.relation))
    }

    subject?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.subject))
    }

    requires?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.requires))
    }

    return relations
}
