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
    val harvest: HarvestMetadata?,
    val associativeRelation: List<AssociativeRelation>?,
    val closeMatch: List<String>?,
    val exactMatch: List<String>?,
    val genericRelation: List<GenericRelation>?,
    val isReplacedBy: List<String>?,
    val memberOf: List<String>?,
    val partitiveRelation: List<PartitiveRelation>?,
    val replaces: List<String>?,
    val seeAlso: List<String>?
)
