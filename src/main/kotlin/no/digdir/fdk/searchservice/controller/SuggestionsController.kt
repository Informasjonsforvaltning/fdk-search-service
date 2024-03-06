package no.digdir.fdk.searchservice.controller

import no.digdir.fdk.searchservice.mapper.pathVariableToSearchType
import no.digdir.fdk.searchservice.model.Suggestion
import no.digdir.fdk.searchservice.service.SuggestionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping(value = ["/suggestions"], produces = ["application/json"])
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

    @GetMapping(
        value = ["/{searchTypes}"]
    )
    fun suggestionsForSpecificResource(
        @PathVariable searchTypes: String,
        @RequestParam("q") query: String,
    ): ResponseEntity<List<Suggestion>> = (
        if (searchTypes.pathVariableToSearchType() == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity(
                suggestionService.suggestResources(query, searchTypes.pathVariableToSearchType()),
                HttpStatus.OK
            )
        })
}
