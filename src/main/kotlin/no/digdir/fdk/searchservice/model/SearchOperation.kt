package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchOperation(
    val query: String? = null,
    val filters: SearchFilters? = null,
    val fields: QueryFields = QueryFields(),
    val sort: SortField? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
class QueryFields(
    val title: Boolean = true,
    val description: Boolean = true,
    val keyword: Boolean = true,
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
    val formats: SearchFilter<List<String>>?,
    val relations: SearchFilter<String>?,
    val last_x_days: SearchFilter<Int>?
)

class SortField(
    val field: SortFieldEnum,
    val direction: SortDirection,
)

enum class SortFieldEnum {
    FIRST_HARVESTED
}

enum class SortDirection {
    ASC,
    DESC,
}

@JsonIgnoreProperties(ignoreUnknown = true)
class SearchFilter<T>(
    val value: T
)
