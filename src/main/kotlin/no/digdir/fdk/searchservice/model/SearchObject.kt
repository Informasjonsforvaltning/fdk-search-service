package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Mapping
import org.springframework.data.elasticsearch.annotations.Setting

const val SEARCH_INDEX_NAME = "fdk-search"

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = SEARCH_INDEX_NAME)
@Mapping(mappingPath = "/elastic/mapping.json")
@Setting(settingPath = "/elastic/settings.json")
data class SearchObject(
    @Id
    val id: String,
    val uri: String,
    val accessRights: ReferenceDataCode?,
    val catalog: Catalog?,
    val dataTheme: List<EuDataTheme>?,
    val description: LocalizedStrings?,
    val fdkFormatPrefixed: List<String>?,
    val metadata: Metadata?,
    val isOpenData: Boolean?,
    val keyword: List<LocalizedStrings>?,
    val losTheme: List<LosNode>?,
    val organization: Organization?,
    val provenance: ReferenceDataCode?,
    val searchType: SearchType,
    val spatial: List<ReferenceDataCode>?,
    val title: LocalizedStrings?,
    val relations: List<Relation>?
)
