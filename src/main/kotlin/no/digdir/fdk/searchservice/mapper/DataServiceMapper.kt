package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

fun DataService.toSearchObject(timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = uri,
        accessRights = accessRights,
        catalog = catalog,
        dataTheme = theme,
        description = description,
        fdkFormatPrefixed = null,
        metadata = harvest?.toMetadata(timestamp, deleted),
        isOpenData = null,
        keyword = keyword,
        losTheme = losTheme,
        organization = publisher,
        provenance = null,
        searchType = SearchType.DATA_SERVICE,
        spatial = null,
        title = title
    )
