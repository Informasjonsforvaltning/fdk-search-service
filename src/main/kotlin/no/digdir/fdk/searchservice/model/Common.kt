package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class LocalizedStrings(
  val nb: String?,
  val nn: String?,
  val en: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class EuDataTheme(
  val title: LocalizedStrings?,
  val code: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class LosNode(
  val name: LocalizedStrings?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Publisher(
  val name: String?,
  val prefLabel: LocalizedStrings?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ReferenceDataCode(
  val code: String,
  val prefLabel: LocalizedStrings?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Subject(
  val altLabel: LocalizedStrings?,
  val definition: LocalizedStrings?,
  val prefLabel: LocalizedStrings?,
  val label: LocalizedStrings?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Distribution(
  val title: LocalizedStrings?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class TextAndURI(
  val text: LocalizedStrings?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Sources(
  val text: List<TextAndURI>?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Definition(
  val text: LocalizedStrings?,
  val sources: Sources?,
  val sourceRelationship: String?,
)
