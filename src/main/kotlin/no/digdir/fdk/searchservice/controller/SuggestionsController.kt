package no.digdir.fdk.searchservice.controller

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import no.digdir.fdk.searchservice.mapper.pathVariableToSearchType
import no.digdir.fdk.searchservice.model.SearchProfile
import no.digdir.fdk.searchservice.model.SuggestionsResult
import no.digdir.fdk.searchservice.service.SuggestionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/suggestions"], produces = ["application/json"])
class SuggestionsController(
    private val suggestionService: SuggestionService
) {
    @GetMapping
    fun suggestResource(
        @RequestParam(value = "q") query: String,
        @RequestParam(value = "profile") searchProfile: SearchProfile?,
        @RequestParam(value = "org") org: String?,
    ): ResponseEntity<SuggestionsResult> =
        ResponseEntity(
            suggestionService.suggestResources(query, null, searchProfile, org),
            HttpStatus.OK
        )

    @Parameter(name ="searchTypes", `in` = ParameterIn.PATH, description ="Available values: concepts, datasets, dataservices, data-services, informationmodels, information-models, services, events, public-services-and-events, services-and-events")
    @GetMapping(value = ["/{searchTypes}"])
    fun suggestionsForSpecificResource(
        @PathVariable searchTypes: String,
        @RequestParam("q") query: String,
        @RequestParam("profile") searchProfile: SearchProfile?,
        @RequestParam(value = "org") org: String?,
    ): ResponseEntity<SuggestionsResult> = (
        if (searchTypes.pathVariableToSearchType() == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity(
                suggestionService.suggestResources(query, searchTypes.pathVariableToSearchType(), searchProfile, org),
                HttpStatus.OK
            )
        })
}
