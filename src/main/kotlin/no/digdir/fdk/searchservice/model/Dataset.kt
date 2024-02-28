package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id

@JsonIgnoreProperties(ignoreUnknown = true)
data class Dataset(
    @Id
    val id: String,
    val uri: String,
    val title: LocalizedStrings?,
    val catalog: Catalog?,
    val description: LocalizedStrings?,
    val keyword: List<LocalizedStrings>?,
    val theme: List<EuDataTheme>?,
    val losTheme: List<LosNode>?,
    val publisher: Organization?,
    val accessRights: ReferenceDataCode?,
    val isOpenData: Boolean?,
    val spatial: List<ReferenceDataCode>?,
    val provenance: ReferenceDataCode?,
    val harvest: HarvestMetadata?,
    val distribution: List<Distribution>?,
    val conformsTo: List<ObjectWithURI>?,
    val inSeries: ObjectWithURI?,
    val informationModel: List<ObjectWithURI>?,
    val references: List<Reference>?,
    val subject: List<ObjectWithURI>?
)
