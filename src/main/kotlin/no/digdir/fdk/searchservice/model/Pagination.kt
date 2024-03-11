package no.digdir.fdk.searchservice.model

import org.springframework.data.domain.Pageable

data class Pagination(
    private val page: Int = 0,
    private val size: Int = 10
) {
    fun getPage(): Int = page.let { if (it < 0) 0 else it }
    fun getSize(): Int = size.let { if (it < 1) 10 else it }
}

fun Pagination.toPageable(): Pageable =
    Pageable.ofSize(getSize())
        .withPage(getPage())
