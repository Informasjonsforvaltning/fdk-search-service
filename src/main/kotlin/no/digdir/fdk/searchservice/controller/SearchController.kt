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
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.model.SearchResult
import no.digdir.fdk.searchservice.service.SearchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/search"], produces = ["application/json"])
@Tag(name = "Search", description = "API for searching resources in the data catalog")
class SearchController(
    private val searchService: SearchService,
) {
    @PostMapping
    @Operation(
        summary = "Search all resources",
        description =
            "Perform a full-text search across all resource types in the data catalog. " +
                "Supports advanced filtering, pagination, sorting, and field-specific querying. " +
                "The search operation allows you to search across titles, descriptions, keywords, and additional metadata, " +
                "apply filters for access rights, data themes, spatial coverage, organization, formats, and more, " +
                "paginate through results with configurable page size, sort results by various criteria, " +
                "get aggregation counts for faceted search, and use search profiles for domain-specific use cases.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully performed search",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = SearchResult::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid search request",
            ),
        ],
    )
    fun search(
        @Parameter(
            description = "Search operation containing query, filters, pagination, sorting, and other search parameters",
            required = true,
        )
        @RequestBody query: SearchOperation,
    ): ResponseEntity<SearchResult> =
        ResponseEntity(
            searchService.search(query, null),
            HttpStatus.OK,
        )

    @PostMapping(value = ["/{searchTypes}"])
    @Operation(
        summary = "Search specific resource types",
        description =
            "Perform a full-text search within specific resource types. " +
                "This endpoint allows you to limit your search to one or more resource types. " +
                "Available resource types: concepts (controlled vocabularies and taxonomies), " +
                "datasets (structured data collections), dataservices/data-services (APIs and data services), " +
                "informationmodels/information-models (data schemas and specifications), services (service descriptions), " +
                "events (event information), public-services-and-events, and services-and-events. " +
                "Supports the same advanced features as the general search endpoint including filtering, pagination, sorting, and aggregations.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully performed search",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = SearchResult::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid search request",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Invalid resource type specified",
            ),
        ],
    )
    fun searchInSpecificResource(
        @Parameter(
            description =
                "Resource type(s) to search in. Available values: concepts, datasets, dataservices, " +
                    "data-services, informationmodels, information-models, services, events, " +
                    "public-services-and-events, services-and-events",
            required = true,
            `in` = ParameterIn.PATH,
            example = "datasets",
        )
        @PathVariable searchTypes: String,
        @Parameter(
            description = "Search operation containing query, filters, pagination, sorting, and other search parameters",
            required = true,
        )
        @RequestBody query: SearchOperation,
    ): ResponseEntity<SearchResult> {
        val searchTypeList =
            searchTypes.pathVariableToSearchType()
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity(
            searchService.search(query, searchTypeList),
            HttpStatus.OK,
        )
    }
}
