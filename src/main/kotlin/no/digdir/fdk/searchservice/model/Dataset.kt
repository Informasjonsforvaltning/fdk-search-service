package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import no.digdir.fdk.searchservice.config.DATASET_INDEX_NAME
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = DATASET_INDEX_NAME)
data class Dataset(
    @Id
    val id: String,
    val title: LocalizedStrings?,
    val description: LocalizedStrings?,
    val objective: LocalizedStrings?,
    val keyword: List<LocalizedStrings>?,
    val theme:List<EuDataTheme>?,
    val losTheme: List<LosNode>?,
    val publisher: Publisher?,
    val accessRights: ReferenceDataCode?,
    val subject: List<Subject>?,
    val distribution: List<Distribution>?,
    val isOpenData: Boolean?
)
