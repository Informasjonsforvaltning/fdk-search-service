package no.digdir.fdk.searchservice.controller

import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.service.SearchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value=["/search"], produces = ["application/json"])
class SearchController(
    private val searchService: SearchService
) {
    @PostMapping
    fun search(@RequestBody query: SearchOperation): ResponseEntity<List<SearchObject>> =
       ResponseEntity(
           searchService.search(query, null),
           HttpStatus.OK
       )

    @PostMapping(value=["/datasets"])
    fun searchDatasets(@RequestBody query: SearchOperation): ResponseEntity<List<SearchObject>> =
       ResponseEntity(
           searchService.search(query, SearchType.DATASET),
           HttpStatus.OK
       )

    @PostMapping(value=["/concepts"])
    fun searchConcepts(@RequestBody query: SearchOperation): ResponseEntity<List<SearchObject>> =
       ResponseEntity(
           searchService.search(query, SearchType.CONCEPT),
           HttpStatus.OK
       )
}
