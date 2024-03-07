package no.digdir.fdk.searchservice.model

data class SearchResult(
    val hits: List<SearchObject>,
    val aggregations: Map<String, List<BucketCount>>,
    val page: PageMeta
)

data class PageMeta(
    val currentPage: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Long
)

data class BucketCount(
    val key: String,
    val count: Long
)
