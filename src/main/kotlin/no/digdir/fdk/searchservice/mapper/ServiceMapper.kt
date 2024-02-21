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
        title = title
    )

private fun Service.getOrganization() = if (hasCompetantAuthority.isNullOrEmpty()) {
    ownedBy?.get(0)
} else {
    hasCompetantAuthority.get(0)
}