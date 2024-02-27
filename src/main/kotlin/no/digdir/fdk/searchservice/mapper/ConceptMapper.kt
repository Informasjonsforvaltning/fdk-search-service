package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.Concept
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType

fun Concept.toSearchObject(timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = identifier,
        accessRights = null,
        catalog = collection?.toCatalog(),
        dataTheme = null,
        description = definition?.text,
        fdkFormatPrefixed = null,
        metadata = harvest?.toMetadata(timestamp, deleted),
        isOpenData = null,
        keyword = null,
        losTheme = null,
        organization = publisher,
        provenance = null,
        searchType = SearchType.CONCEPT,
        spatial = null,
        title = prefLabel
    )
