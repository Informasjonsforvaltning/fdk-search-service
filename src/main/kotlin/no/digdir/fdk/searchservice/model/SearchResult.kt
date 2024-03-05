package no.digdir.fdk.searchservice.model

data class SearchResult(
    val hits: List<SearchObject>,
    val page: PageMeta
)

data class PageMeta(
    val currentPage: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Long
)
