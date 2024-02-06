package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import no.digdir.fdk.searchservice.config.CONCEPT_INDEX_NAME
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = CONCEPT_INDEX_NAME)
data class Concept(
    @Id
    val id: String,
    val publisher: Publisher?,
    val definition: Definition?,
    val prefLabel: LocalizedStrings?,
    val subject: List<Subject>?,
)
