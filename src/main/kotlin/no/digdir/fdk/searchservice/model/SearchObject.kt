package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
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
    // the id field is the fdk-id for the resource
    @Id
    val id: String,
    val uri: String?,
    val accessRights: ReferenceDataCode?,
    val catalog: Catalog?,
    val dataTheme: Set<EuDataTheme>?,
    val description: LocalizedStrings?,
    val fdkFormatPrefixed: Set<String>?,
    val metadata: Metadata?,
    @get:JsonProperty("isOpenData")
    @field:JsonProperty("isOpenData")
    val isOpenData: Boolean?,
    val keyword: Set<LocalizedStrings>?,
    val losTheme: Set<LosNode>?,
    val organization: Organization?,
    val provenance: ReferenceDataCode?,
    val searchType: SearchType,
    val spatial: Set<ReferenceDataCode>?,
    val title: LocalizedStrings?,
    val additionalTitles: Set<LocalizedStrings>?,
    val relations: Set<Relation>?,
    val specializedType: SpecializedType?,
    val isAuthoritative: Boolean?,
    @get:JsonProperty("isRelatedToTransportportal")
    @field:JsonProperty("isRelatedToTransportportal")
    val isRelatedToTransportportal: Boolean?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Relation(
    val uri: String?,
    val type: RelationType?,
)

/** Wire format is camelCase in Elasticsearch and the API; see elasticsearch converters. */
enum class RelationType(
    @get:JsonValue val value: String,
) {
    ASSOCIATIVE_RELATION("associativeRelation"),
    CLOSE_MATCH("closeMatch"),
    EXACT_MATCH("exactMatch"),
    GENERALIZES("generalizes"),
    SPECIALIZES("specializes"),
    IS_REPLACED_BY("isReplacedBy"),
    MEMBER_OF("memberOf"),
    REPLACES("replaces"),
    SEE_ALSO("seeAlso"),
    CONFORMS_TO("conformsTo"),
    SERVES_DATASET("servesDataset"),
    IN_SERIES("inSeries"),
    SUBJECT("subject"),
    HAS_PART("hasPart"),
    IS_PART_OF("isPartOf"),
    IS_GROUPED_BY("isGroupedBy"),
    IS_CLASSIFIED_BY("isClassifiedBy"),
    IS_DESCRIBED_AT("isDescribedAt"),
    RELATION("relation"),
    HAS_VERSION("hasVersion"),
    IS_VERSION_OF("isVersionOf"),
    REFERENCES("references"),
    IS_REFERENCED_BY("isReferencedBy"),
    REQUIRES("requires"),
    IS_REQUIRED_BY("isRequiredBy"),
    SOURCE("source"),
    ;

    companion object {
        private val byValue = entries.associateBy { it.value }
        private val byName = entries.associateBy { it.name }

        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): RelationType =
            byValue[value]
                ?: byName[value]
                ?: throw IllegalArgumentException("Unknown RelationType: $value")
    }
}
