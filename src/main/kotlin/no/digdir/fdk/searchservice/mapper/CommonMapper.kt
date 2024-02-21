package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.Catalog
import no.digdir.fdk.searchservice.model.Collection
import no.digdir.fdk.searchservice.model.HarvestMetadata
import no.digdir.fdk.searchservice.model.Metadata

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
