package no.digdir.fdk.searchservice.controller

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.ExampleObject
import no.digdir.fdk.searchservice.mapper.pathVariableToSearchType
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.model.SearchResult
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
    fun search(@RequestBody query: SearchOperation): ResponseEntity<SearchResult> =
        ResponseEntity(
            searchService.search(query, null),
            HttpStatus.OK
        )

    /**
     * Search in specific resource
     * @param query SearchOperation object containing query and filters
     * @param searchTypes Type of resource to search in (dataservices, informationmodels, concepts, events, datasets, services)
     * @return List of SearchObject
     */
    @Parameter(name ="searchTypes", `in` = ParameterIn.PATH, description ="Available values: concepts, datasets, dataservices, data-services, informationmodels, information-models, services, events, public-services-and-events, services-and-events")
    @PostMapping(value = ["/{searchTypes}"])
    fun searchInSpecificResource(
        @RequestBody query: SearchOperation,
        @PathVariable searchTypes: String
    ): ResponseEntity<SearchResult> {
        val searchTypeList = searchTypes.pathVariableToSearchType()
        return if (searchTypeList == null) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else {
            ResponseEntity(
                searchService.search(query, searchTypeList),
                HttpStatus.OK
            )
        }
    }
}
