package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchOperation(
  val query: String? = null,
  val filters: SearchFilters? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchFilters(
  val opendata: SearchFilter<Boolean>?,
  val accessRights: SearchFilter<String>?,
  val theme: SearchFilter<List<String>>?,
  val spatial: SearchFilter<String>?,
  val provenance: SearchFilter<String>?,
  val los: SearchFilter<String>?,
  val orgPath: SearchFilter<String>?,
  val formats: SearchFilter<List<String>>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
class SearchFilter<T>(
  val value: T
)
