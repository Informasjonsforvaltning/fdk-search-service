package no.digdir.fdk.searchservice.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchOperation(
    val query: String? = null,
    val filters: SearchFilters? = null,
    val fields: QueryFields = QueryFields(),
    val sort: SortField? = null,
    val pagination: Pagination = Pagination(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
class QueryFields(
    val title: Boolean = true,
    val description: Boolean = true,
    val keyword: Boolean = true,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchFilters(
    val openData: SearchFilter<Boolean>?,
    val accessRights: SearchFilter<String>?,
    val dataTheme: SearchFilter<List<String>>?,
    val spatial: SearchFilter<List<String>>?,
    val provenance: SearchFilter<String>?,
    val losTheme: SearchFilter<List<String>>?,
    val orgPath: SearchFilter<String>?,
    val formats: SearchFilter<List<String>>?,
    val relations: SearchFilter<String>?,
    val lastXDays: SearchFilter<Int>?,
    val uri: SearchFilter<List<String>>?
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
