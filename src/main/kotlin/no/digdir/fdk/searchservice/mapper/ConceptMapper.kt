package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*

fun Concept.toSearchObject(id: String, timestamp: Long, deleted: Boolean = false) =
    SearchObject(
        id = id,
        uri = uri,
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
        relations = getRelations(),
        specializedType = null,
        isAuthoritative = null
    )

fun Concept.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()

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
