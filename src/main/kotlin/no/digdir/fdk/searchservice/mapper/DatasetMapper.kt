package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

fun Dataset.toSearchObject(timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = uri,
        accessRights = accessRights,
        catalog = catalog,
        dataTheme = theme,
        description = description,
        fdkFormatPrefixed = null,
        metadata = harvest?.toMetadata(timestamp, deleted),
        isOpenData = isOpenData,
        keyword = keyword,
        losTheme = losTheme,
        organization = publisher,
        provenance = provenance,
        searchType = SearchType.DATASET,
        spatial = spatial,
        title = title
    )
