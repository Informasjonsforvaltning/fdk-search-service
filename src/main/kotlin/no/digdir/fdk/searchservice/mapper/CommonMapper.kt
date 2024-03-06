package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.*
import no.digdir.fdk.searchservice.model.Collection

fun Collection.toCatalog() =
    Catalog(
        id = id,
        uri = uri,
        description = description,
        publisher = publisher,
        title = label
    )

fun HarvestMetadata.toMetadata(timestamp: Long, deleted: Boolean = false) =
    Metadata(
        firstHarvested = firstHarvested,
        changed = changed,
        deleted = deleted,
        timestamp = timestamp
    )

fun String.pathVariableToSearchType(): List<SearchType>? =
    when (this) {
        "concepts" -> listOf(SearchType.CONCEPT)
        "datasets" -> listOf(SearchType.DATASET)
        "dataservices" -> listOf(SearchType.DATA_SERVICE)
        "informationmodels" -> listOf(SearchType.INFORMATION_MODEL)
        "services" -> listOf(SearchType.SERVICE)
        "events" -> listOf(SearchType.EVENT)
        "public-services-and-events" -> listOf(SearchType.SERVICE, SearchType.EVENT)
        "services-and-events" -> listOf(SearchType.SERVICE, SearchType.EVENT)
        else -> null
    }
