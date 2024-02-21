package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id


@JsonIgnoreProperties(ignoreUnknown = true)
data class DataService(
    @Id
    val id: String,
    val uri: String,
    val title: LocalizedStrings?,
    val catalog: Catalog?,
    val description: LocalizedStrings?,
    val keyword: List<LocalizedStrings>?,
    val theme:List<EuDataTheme>?,
    val losTheme: List<LosNode>?,
    val publisher: Organization?,
    val accessRights: ReferenceDataCode?,
    val harvest: HarvestMetadata?,
)
