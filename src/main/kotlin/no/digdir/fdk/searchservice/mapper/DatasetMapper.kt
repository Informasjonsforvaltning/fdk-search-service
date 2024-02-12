package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

fun Dataset.toSearchObject() =
    SearchObject(
        id = id,
        uri = uri,
        accessRights = accessRights,
        catalog = catalog,
        dataTheme = theme,
        description = description,
        fdkFormatPrefixed = null,
        harvest = harvest,
        isOpenData = isOpenData,
        keyword = keyword,
        losTheme = losTheme,
        organization = publisher,
        provenance = provenance,
        searchType = SearchType.DATASET,
        spatial = spatial,
        title = title,
    )
