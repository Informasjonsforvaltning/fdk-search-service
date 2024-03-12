package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id


@JsonIgnoreProperties(ignoreUnknown = true)
data class Event(
    val uri: String,
    val title: LocalizedStrings?,
    val catalog: Catalog?,
    val description: LocalizedStrings?,
    val harvest: HarvestMetadata?,
    val subject: List<String>?
)
