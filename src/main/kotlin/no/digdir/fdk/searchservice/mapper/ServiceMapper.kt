package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

fun Service.toSearchObject(id: String, timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = uri,
        accessRights = null,
        catalog = catalog,
        dataTheme = euDataThemes?.toSet(),
        description = description,
        fdkFormatPrefixed = null,
        metadata = harvest?.toMetadata(timestamp, deleted),
        isOpenData = null,
        keyword = keyword?.toSet(),
        losTheme = losTheme?.toSet(),
        organization = getOrganization()?.toSearchOrg(),
        provenance = null,
        searchType = SearchType.SERVICE,
        spatial = spatial?.toSet(),
        title = title,
        relations = getRelations(),
        specializedType = null,
        isAuthoritative = null,
        isRelatedToTransportportal = false,
        additionalTitles = null
    )

private fun Service.getOrganization() = if (hasCompetentAuthority.isNullOrEmpty()) {
    ownedBy?.get(0)
} else {
    hasCompetentAuthority[0]
}

private fun ServiceOrganization.toSearchOrg(): Organization =
    Organization(
        id = identifier,
        uri = uri,
        orgPath = orgPath,
        name = name?.nb,
        prefLabel = title
    )


fun Service.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()

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
