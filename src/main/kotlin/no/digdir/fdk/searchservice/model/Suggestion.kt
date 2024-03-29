package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Suggestion(
    val id: String,
    val title: LocalizedStrings?,
    val description: LocalizedStrings?,
    val uri: String?,
    val organization: Organization?,
    val searchType: SearchType
)
