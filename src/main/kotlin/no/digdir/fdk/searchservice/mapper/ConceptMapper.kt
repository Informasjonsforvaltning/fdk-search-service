package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

fun Concept.toSearchObject() =
    SearchObject(
        id = id,
        uri = identifier,
        accessRights = null,
        catalog = collection?.toCatalog(),
        dataTheme = null,
        description = definition?.text,
        fdkFormatPrefixed = null,
        harvest = harvest,
        isOpenData = null,
        keyword = null,
        losTheme = null,
        organization = publisher,
        provenance = null,
        searchType = SearchType.CONCEPT,
        spatial = null,
        title = prefLabel,
    )
