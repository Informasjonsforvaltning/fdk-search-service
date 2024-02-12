package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

const val SEARCH_INDEX_NAME = "fdk-search"

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = SEARCH_INDEX_NAME)
data class SearchObject(
    @Id
    val id: String,
    val uri: String,
    val accessRights: ReferenceDataCode?,
    val catalog: Catalog?,
    val dataTheme: List<EuDataTheme>?,
    val description: LocalizedStrings?,
    val fdkFormatPrefixed: List<String>?,
    val harvest: HarvestMetaData?,
    val isOpenData: Boolean?,
    val keyword: List<LocalizedStrings>?,
    val losTheme: List<LosNode>?,
    val organization: Organization?,
    val provenance: ReferenceDataCode?,
    val searchType: SearchType,
    val spatial: List<ReferenceDataCode>?,
    val title: LocalizedStrings?,
)
