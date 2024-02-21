package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id

@JsonIgnoreProperties(ignoreUnknown = true)
data class Suggestion(
    val id: String,
    val title: LocalizedStrings?,
    val uri: String,
    val searchType: SearchType
)