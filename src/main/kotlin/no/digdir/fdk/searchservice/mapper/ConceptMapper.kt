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
        title = prefLabel,
        relations = getRelations()
    )

private fun Concept.getRelations(): List<Relation> {
    val relations: MutableList<Relation> = mutableListOf()

    associativeRelation?.forEach {
        relations.add(Relation(uri = it.related, type = "associativeRelation"))
    }

    closeMatch?.forEach {
        relations.add(Relation(uri = it, type = "closeMatch"))
    }

    exactMatch?.forEach {
        relations.add(Relation(uri = it, type = "exactMatch"))
    }

    genericRelation?.forEach {
        relations.add(Relation(uri = it.generalizes, type = "genericRelation"))
    }

    isReplacedBy?.forEach {
        relations.add(Relation(uri = it, type = "isReplacedBy"))
    }

    memberOf?.forEach {
        relations.add(Relation(uri = it, type = "memberOf"))
    }
    partitiveRelation?.forEach {
        relations.add(Relation(uri = it.isPartOf, type = "partitiveRelation"))
    }

    replaces?.forEach {
        relations.add(Relation(uri = it, type = "replaces"))
    }

    seeAlso?.forEach {
        relations.add(Relation(uri = it, type = "seeAlso"))
    }

    return relations
}
