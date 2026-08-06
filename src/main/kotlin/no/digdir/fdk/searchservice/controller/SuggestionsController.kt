package no.digdir.fdk.searchservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import no.digdir.fdk.searchservice.mapper.pathVariableToSearchType
import no.digdir.fdk.searchservice.model.SearchProfile
import no.digdir.fdk.searchservice.model.SuggestionsResult
import no.digdir.fdk.searchservice.service.SuggestionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/suggestions"], produces = ["application/json"])
@Tag(name = "Suggestions", description = "API for getting search suggestions and autocomplete results")
class SuggestionsController(
    private val suggestionService: SuggestionService
) {
    @GetMapping
    @Operation(
        summary = "Get search suggestions for all resources",
        description = "Retrieve search suggestions (autocomplete) across all resource types in the data catalog. " +
                "This endpoint is useful for implementing autocomplete functionality in search interfaces. " +
                "The suggestions are based on matching titles, descriptions, and other metadata fields. " +
                "You can optionally filter suggestions by search profile (e.g., TRANSPORT for transport-specific suggestions) " +
                "or organization (limit suggestions to resources from a specific organization).",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved suggestions",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = SuggestionsResult::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters",
            ),
        ],
    )
    fun suggestResource(
        @Parameter(
            description = "Search query string for which to get suggestions",
            required = true,
            example = "transport",
        )
        @RequestParam(value = "q") query: String,
        @Parameter(
            description = "Optional search profile to apply domain-specific filtering (e.g., TRANSPORT)",
            required = false,
        )
        @RequestParam(value = "profile") searchProfile: SearchProfile?,
        @Parameter(
            description = "Optional organization identifier to limit suggestions to resources from a specific organization",
            required = false,
        )
        @RequestParam(value = "org") org: String?,
    ): ResponseEntity<SuggestionsResult> =
        ResponseEntity(
            suggestionService.suggestResources(query, null, searchProfile, org),
            HttpStatus.OK
        )

    @GetMapping(value = ["/{searchTypes}"])
    @Operation(
        summary = "Get search suggestions for specific resource types",
        description = "Retrieve search suggestions (autocomplete) for specific resource types. " +
                "This endpoint allows you to limit suggestions to one or more resource types. " +
                "Available resource types: concepts (controlled vocabularies and taxonomies), " +
                "datasets (structured data collections), dataservices/data-services (APIs and data services), " +
                "informationmodels/information-models (data schemas and specifications), services (service descriptions), " +
                "events (event information), public-services-and-events, and services-and-events. " +
                "The suggestions are based on matching titles, descriptions, and other metadata fields. " +
                "You can optionally filter suggestions by search profile and organization.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved suggestions",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = SuggestionsResult::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Invalid resource type specified",
            ),
        ],
    )
    fun suggestionsForSpecificResource(
        @Parameter(
            description = "Resource type(s) to get suggestions for. Available values: concepts, datasets, dataservices, data-services, informationmodels, information-models, services, events, public-services-and-events, services-and-events",
            required = true,
            `in` = ParameterIn.PATH,
            example = "datasets",
        )
        @PathVariable searchTypes: String,
        @Parameter(
            description = "Search query string for which to get suggestions",
            required = true,
            example = "transport",
        )
        @RequestParam("q") query: String,
        @Parameter(
            description = "Optional search profile to apply domain-specific filtering (e.g., TRANSPORT)",
            required = false,
        )
        @RequestParam("profile") searchProfile: SearchProfile?,
        @Parameter(
            description = "Optional organization identifier to limit suggestions to resources from a specific organization",
            required = false,
        )
        @RequestParam(value = "org") org: String?,
    ): ResponseEntity<SuggestionsResult> {
        val searchTypeList = searchTypes.pathVariableToSearchType()
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity(
            suggestionService.suggestResources(query, searchTypeList, searchProfile, org),
            HttpStatus.OK
        )
    }
}
