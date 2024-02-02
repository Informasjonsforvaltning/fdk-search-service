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
    val title: LocalizedStrings? = null,
    val description: LocalizedStrings? = null,
    val objective: LocalizedStrings? = null,
    val keyword: List<LocalizedStrings>? = null,
    val theme:List<EuDataTheme>? = null,
    val losTheme: List<LosNode>? = null,
    val publisher: Publisher? = null,
    val accessRights: ReferenceDataCode? = null,
    val subject: List<Subject>? = null,
    val distribution: List<Distribution>? = null,
)
