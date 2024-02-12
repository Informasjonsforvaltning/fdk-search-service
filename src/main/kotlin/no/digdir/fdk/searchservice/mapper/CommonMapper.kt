package no.digdir.fdk.searchservice.mapper

import no.digdir.fdk.searchservice.model.Catalog
import no.digdir.fdk.searchservice.model.Collection

fun Collection.toCatalog() =
    Catalog(
        id = id,
        uri = uri,
        description = description,
        publisher = publisher,
        title = label
    )
