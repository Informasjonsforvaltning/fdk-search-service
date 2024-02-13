package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchOperation(
  val query: String?,
  val filters: SearchFilters?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchFilters(
  val opendata: Boolean?,
  val accessRights: String?
)
