package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

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
        relations.add(Relation(uri = it.related, type = RelationType.associativeRelation))
    }

    closeMatch?.forEach {
        relations.add(Relation(uri = it, type = RelationType.closeMatch))
    }

    exactMatch?.forEach {
        relations.add(Relation(uri = it, type = RelationType.exactMatch))
    }

    genericRelation?.forEach { relation ->
        relation.generalizes?.let { generalizes ->
            relations.add(Relation(uri = generalizes, type = RelationType.generalizes))
        }
        relation.specializes?.let { specializes ->
            relations.add(Relation(uri = specializes, type = RelationType.specializes))
        }
    }

    isReplacedBy?.forEach {
        relations.add(Relation(uri = it, type = RelationType.isReplacedBy))
    }

    memberOf?.forEach {
        relations.add(Relation(uri = it, type = RelationType.memberOf))
    }

    partitiveRelation?.forEach { relation ->
        relation.hasPart?.let { hasPart ->
            relations.add(Relation(uri = hasPart, type = RelationType.hasPart))
        }
        relation.isPartOf?.let { isPartOf ->
            relations.add(Relation(uri = isPartOf, type = RelationType.isPartOf))
        }
    }

    replaces?.forEach {
        relations.add(Relation(uri = it, type = RelationType.replaces))
    }

    seeAlso?.forEach {
        relations.add(Relation(uri = it, type = RelationType.seeAlso))
    }

    return relations
}
