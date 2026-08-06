package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.Organization
import no.digdir.fdk.searchservice.model.Relation
import no.digdir.fdk.searchservice.model.RelationType
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.Service
import no.digdir.fdk.searchservice.model.ServiceOrganization
import no.digdir.fdk.searchservice.model.SpecializedType

fun Service.toSearchObject(
    id: String,
    timestamp: Long,
    deleted: Boolean = false,
) = SearchObject(
    id = id,
    uri = uri,
    accessRights = null,
    catalog = catalog,
    dataTheme = euDataThemes?.toSet(),
    description = description,
    fdkFormatPrefixed = null,
    metadata = harvest?.toMetadata(timestamp, deleted),
    isOpenData = null,
    keyword = keyword?.toSet(),
    losTheme = losTheme?.toSet(),
    organization = getOrganization()?.toSearchOrg(),
    provenance = null,
    searchType = SearchType.SERVICE,
    spatial = spatial?.toSet(),
    title = title,
    relations = getRelations(),
    specializedType = getSpecializedType(),
    isAuthoritative = null,
    isRelatedToTransportportal = false,
    additionalTitles = null,
)

private fun Service.getOrganization() =
    if (hasCompetentAuthority.isNullOrEmpty()) {
        ownedBy?.get(0)
    } else {
        hasCompetentAuthority[0]
    }

private fun ServiceOrganization.toSearchOrg(): Organization =
    Organization(
        id = identifier,
        uri = uri,
        orgPath = orgPath,
        name = title?.nb ?: prefLabel?.nb,
        prefLabel = prefLabel ?: title,
    )

fun Service.getRelations(): Set<Relation> {
    val relations: MutableSet<Relation> = mutableSetOf()

    isGroupedBy?.forEach {
        relations.add(Relation(uri = it, type = RelationType.IS_GROUPED_BY))
    }

    isClassifiedBy?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.IS_CLASSIFIED_BY))
    }

    isDescribedAt?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.IS_DESCRIBED_AT))
    }

    relation?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.RELATION))
    }

    subject?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.SUBJECT))
    }

    requires?.forEach {
        relations.add(Relation(uri = it.uri, type = RelationType.REQUIRES))
    }

    return relations
}

fun Service.getSpecializedType(): SpecializedType? =
    when (specializedType) {
        "publicService" -> SpecializedType.PUBLIC_SERVICE
        "service" -> SpecializedType.SERVICE
        else -> null
    }
