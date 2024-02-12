package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id


@JsonIgnoreProperties(ignoreUnknown = true)
data class Concept(
    @Id
    val id: String,
    val identifier: String,
    val collection: Collection?,
    val publisher: Organization?,
    val definition: Definition?,
    val prefLabel: LocalizedStrings?,
    val harvest: HarvestMetaData?
)
