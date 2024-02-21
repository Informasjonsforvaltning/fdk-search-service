package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

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
        title = title
    )