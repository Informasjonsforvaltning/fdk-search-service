package no.digdir.fdk.searchservice.controller

import no.digdir.fdk.searchservice.mapper.pathVariableToSearchType
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.service.SearchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/search"], produces = ["application/json"])
class SearchController(
    private val searchService: SearchService
) {
    @PostMapping
    fun search(@RequestBody query: SearchOperation): ResponseEntity<List<SearchObject>> =
        ResponseEntity(
            searchService.search(query, null),
            HttpStatus.OK
        )

    /**
     * Search in specific resource
     * @param query SearchOperation object containing query and filters
     * @param searchType Type of resource to search in (dataservices, informationmodels, concepts, events, datasets, services)
     * @return List of SearchObject
     */
    @PostMapping(value = ["/{endpoint}"])
    fun searchInSpecificResource(
        @RequestBody query: SearchOperation,
        @PathVariable endpoint: String
    ): ResponseEntity<List<SearchObject>> {
        val searchTypes = endpoint.pathVariableToSearchType()
        return if (searchTypes == null) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else {
            ResponseEntity(
                searchService.search(query, searchTypes),
                HttpStatus.OK
            )
        }
    }
}
