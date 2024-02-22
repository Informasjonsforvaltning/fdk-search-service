package no.digdir.fdk.searchservice.controller

import no.digdir.fdk.searchservice.model.SearchType
import no.digdir.fdk.searchservice.model.Suggestion
import no.digdir.fdk.searchservice.service.SuggestionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value=["/suggestions"], produces = ["application/json"])
class SuggestionsController(
    private val suggestionService: SuggestionService
) {
    @GetMapping
    fun suggestResource(
        @RequestParam(
            value = "q"
        ) query: String,
    ): ResponseEntity<List<Suggestion>> =
        ResponseEntity(
            suggestionService.suggestResources(query, null),
            HttpStatus.OK
        )

    @GetMapping(value = ["/public_services_and_events"])
    fun suggestPublicServicesAndEvents(
        @RequestParam(
            value = "q"
        ) query: String,
    ): ResponseEntity<List<Suggestion>> {
        val suggestedServices = suggestionService.suggestResources(query, SearchType.SERVICE)
        val suggestedEvents = suggestionService.suggestResources(query, SearchType.EVENT)
        val combinedSuggestions = suggestedServices + suggestedEvents
        return ResponseEntity(combinedSuggestions, HttpStatus.OK)
}


    @GetMapping(
        value = ["/{searchType}"]
    )
    fun suggestionsForSpecificResource(
        @PathVariable searchType: String,
        @RequestParam("q") query: String,
    ): ResponseEntity<List<Suggestion>> = (
        if (searchType.pathVariableToSearchType() == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity(
                suggestionService.suggestResources(query, searchType.pathVariableToSearchType()),
                HttpStatus.OK
            )
        })

    private fun String.pathVariableToSearchType(): SearchType? =
        when (this) {
            "concepts" -> SearchType.CONCEPT
            "datasets" -> SearchType.DATASET
            "dataservices" -> SearchType.DATA_SERVICE
            "informationmodels" -> SearchType.INFORMATION_MODEL
            "services" -> SearchType.SERVICE
            "events" -> SearchType.EVENT
            else -> null
        }
}
