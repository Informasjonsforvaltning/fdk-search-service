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

fun String.pathVariableToSearchType(): SearchType? =
    when (this) {
        "concepts" -> SearchType.CONCEPT
        "datasets" -> SearchType.DATASET
        "dataservices" -> SearchType.DATA_SERVICE
        "informationmodels" -> SearchType.INFORMATION_MODEL
        "services" -> SearchType.SERVICE
        "events" -> SearchType.EVENT
        else -> null
    }
