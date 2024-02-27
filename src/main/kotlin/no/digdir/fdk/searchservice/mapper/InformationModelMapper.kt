package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.InformationModel
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType

fun InformationModel.toSearchObject(timestamp: Long, deleted: Boolean = false) =
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
        searchType = SearchType.INFORMATION_MODEL,
        spatial = null,
        title = title,
        relations = getRelations()
    )

private fun InformationModel.getRelations(): List<Relation> {
    val relations: MutableList<Relation> = mutableListOf()


    hasPart?.let {
        relations.add(Relation(uri = hasPart, type = RelationType.hasPart))
    }

    isPartOf?.let {
        relations.add(Relation(uri = isPartOf, type = RelationType.isPartOf))
    }

    isReplacedBy?.let {
        relations.add(Relation(uri = isReplacedBy, type = RelationType.isReplacedBy))
    }

    replaces?.let {
        relations.add(Relation(uri = it, type = RelationType.replaces))
    }


    subjects?.forEach {
        relations.add(Relation(uri = it, type = RelationType.subjects))
    }

    return relations
}
