package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import no.digdir.fdk.searchservice.config.DATASERVICE_INDEX_NAME
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = DATASERVICE_INDEX_NAME)
data class Dataservice(
    @Id
    val id: String,
    val title: LocalizedStrings?,
    val description: LocalizedStrings?,
    val publisher: Publisher?,
)
