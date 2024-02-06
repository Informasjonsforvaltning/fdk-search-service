package no.digdir.fdk.searchservice.controller

import no.digdir.fdk.searchservice.model.Concept
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.service.ConceptSearchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value=["/concepts"], produces = ["application/json"])
class ConceptController(
    private val conceptSearchService: ConceptSearchService
) {
    @PostMapping
    fun searchConcepts(@RequestBody query: SearchOperation): ResponseEntity<List<Concept>> {
        val concepts: List<Concept> = conceptSearchService.searchConcepts(query)
        return ResponseEntity(concepts, HttpStatus.OK)
    }
}
