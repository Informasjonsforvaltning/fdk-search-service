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
    val closeMatch: Set<String>?,
    val exactMatch: Set<String>?,
    val genericRelation: List<GenericRelation>?,
    val isReplacedBy: Set<String>?,
    val memberOf: Set<String>?,
    val partitiveRelation: List<PartitiveRelation>?,
    val replaces: Set<String>?,
    val seeAlso: Set<String>?
)
