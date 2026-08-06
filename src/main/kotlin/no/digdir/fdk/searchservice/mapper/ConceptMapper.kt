package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.Concept
import no.digdir.fdk.searchservice.model.Relation
import no.digdir.fdk.searchservice.model.RelationType
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType

fun Concept.toSearchObject(
    id: String,
    timestamp: Long,
    deleted: Boolean = false,
) = SearchObject(
    id = id,
    // identifier from concepts are the resource uri
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
    relations = getRelations(),
    specializedType = null,
    isAuthoritative = null,
    isRelatedToTransportportal = false,
    additionalTitles = listOf(hiddenLabel ?: emptyList(), altLabel ?: emptyList()).flatten().toSet(),
)

fun Concept.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()

    associativeRelation?.forEach {
        relations.add(Relation(uri = it.related, type = RelationType.ASSOCIATIVE_RELATION))
    }

    closeMatch?.forEach {
        relations.add(Relation(uri = it, type = RelationType.CLOSE_MATCH))
    }

    exactMatch?.forEach {
        relations.add(Relation(uri = it, type = RelationType.EXACT_MATCH))
    }

    genericRelation?.forEach { relation ->
        relation.generalizes?.let { generalizes ->
            relations.add(Relation(uri = generalizes, type = RelationType.GENERALIZES))
        }
        relation.specializes?.let { specializes ->
            relations.add(Relation(uri = specializes, type = RelationType.SPECIALIZES))
        }
    }

    isReplacedBy?.forEach {
        relations.add(Relation(uri = it, type = RelationType.IS_REPLACED_BY))
    }

    memberOf?.forEach {
        relations.add(Relation(uri = it, type = RelationType.MEMBER_OF))
    }

    partitiveRelation?.forEach { relation ->
        relation.hasPart?.let { hasPart ->
            relations.add(Relation(uri = hasPart, type = RelationType.HAS_PART))
        }
        relation.isPartOf?.let { isPartOf ->
            relations.add(Relation(uri = isPartOf, type = RelationType.IS_PART_OF))
        }
    }

    replaces?.forEach {
        relations.add(Relation(uri = it, type = RelationType.REPLACES))
    }

    seeAlso?.forEach {
        relations.add(Relation(uri = it, type = RelationType.SEE_ALSO))
    }

    return relations
}
