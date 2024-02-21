package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id


@JsonIgnoreProperties(ignoreUnknown = true)
data class Service(
    @Id
    val id: String,
    val uri: String,
    val title: LocalizedStrings?,
    val catalog: Catalog?,
    val description: LocalizedStrings?,
    val keyword: List<LocalizedStrings>?,
    val euDataThemes:List<EuDataTheme>?,
    val losTheme: List<LosNode>?,
    val ownedBy: List<Organization>?,
    val hasCompetantAuthority: List<Organization>?,
    val spatial: List<ReferenceDataCode>?,
    val harvest: HarvestMetadata?,
)
