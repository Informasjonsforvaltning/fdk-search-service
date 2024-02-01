package no.digdir.fdk.searchservice.controller

import no.digdir.fdk.searchservice.elastic.SearchObjectRepository
import no.digdir.fdk.searchservice.model.TempTestDBO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value=["/search"], produces = ["application/json"])
class SearchObjectsController(
    private val searchObjectRepository: SearchObjectRepository
) {
    @GetMapping
    fun getSearchObject(): ResponseEntity<List<TempTestDBO>> {
        val searchObjects: List<TempTestDBO> = searchObjectRepository.findAll().toList()
        return ResponseEntity(searchObjects, HttpStatus.OK)
    }
}
