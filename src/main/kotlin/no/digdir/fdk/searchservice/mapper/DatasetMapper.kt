package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.Dataset
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType

fun Dataset.toSearchObject(timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = uri,
        accessRights = accessRights,
        catalog = catalog,
        dataTheme = theme,
        description = description,
        fdkFormatPrefixed = extractPrefixedFormats(),
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

fun Dataset.extractPrefixedFormats(): List<String> {
    val mutableList = mutableListOf<String>()
    distribution?.forEach { dist ->
        dist.fdkFormat?.forEach { format ->
            mutableList.add("${format.type} ${format.code}")
        }
    }
    return mutableList
}
