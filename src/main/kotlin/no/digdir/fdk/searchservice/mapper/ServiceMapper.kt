package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.Service

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
        relations.add(Relation(uri = it, type = "isGroupedBy"))
    }

    isClassifiedBy?.forEach {
        relations.add(Relation(uri = it.uri, type = "isClassifiedBy"))
    }

    isDescribedAt?.forEach {
        relations.add(Relation(uri = it.uri, type = "isDescribedAt"))
    }

    relation?.forEach {
        relations.add(Relation(uri = it.uri, type = "relation"))
    }

    subject?.forEach {
        relations.add(Relation(uri = it.uri, type = "subject"))
    }

    return relations
}
