package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.DataService
import no.digdir.fdk.searchservice.model.MediaTypeOrExtentType
import no.digdir.fdk.searchservice.model.Relation
import no.digdir.fdk.searchservice.model.RelationType
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType

fun DataService.toSearchObject(
    id: String,
    timestamp: Long,
    deleted: Boolean = false,
) = SearchObject(
    id = id,
    uri = uri,
    accessRights = accessRights,
    catalog = catalog,
    dataTheme = theme?.toSet(),
    description = description,
    fdkFormatPrefixed = extractPrefixedFormats(),
    metadata = harvest?.toMetadata(timestamp, deleted),
    isOpenData = null,
    keyword = keyword?.toSet(),
    losTheme = losTheme?.toSet(),
    organization = publisher,
    provenance = null,
    searchType = SearchType.DATA_SERVICE,
    spatial = null,
    title = title,
    relations = getRelations(),
    specializedType = null,
    isAuthoritative = null,
    isRelatedToTransportportal = false,
    additionalTitles = null,
)

fun DataService.extractPrefixedFormats(): Set<String> {
    val mutableList = mutableSetOf<String>()
    fdkFormat?.forEach { format ->
        if (format.type == MediaTypeOrExtentType.UNKNOWN) {
            mutableList.add(MediaTypeOrExtentType.UNKNOWN.name)
        } else {
            mutableList.add("${format.type} ${format.code}")
        }
    }
    return mutableList
}

fun DataService.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()

    conformsTo?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.CONFORMS_TO))
    }

    servesDataset?.forEach {
        relations.add(Relation(uri = it, type = RelationType.SERVES_DATASET))
    }

    return relations
}
