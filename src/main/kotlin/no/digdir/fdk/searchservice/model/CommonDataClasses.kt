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
  val losPaths: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ReferenceDataCode(
  val uri: String?,
  val code: String,
  val prefLabel: LocalizedStrings?,
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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class HarvestMetadata(
  val firstHarvested: String?,
  val changed: List<String>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Metadata(
  val firstHarvested: String?,
  val changed: List<String>?,
  val deleted: Boolean?,
  val timestamp: Long?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Catalog(
  val description: LocalizedStrings?,
  val id: String?,
  val publisher: Organization?,
  val title: LocalizedStrings?,
  val uri: String?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Collection (
  val description: LocalizedStrings?,
  val id: String?,
  val publisher: Organization?,
  val label: LocalizedStrings?,
  val uri: String?,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Organization(
  val identifier: String?,
  val uri: String?,
  val orgPath: String?,
  val name: String?,
  val prefLabel: LocalizedStrings?,
)
