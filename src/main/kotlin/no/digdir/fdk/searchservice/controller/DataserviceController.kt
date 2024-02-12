package no.digdir.fdk.searchservice.controller

import no.digdir.fdk.searchservice.model.Dataservice
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.service.DataserviceSearchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value=["/dataservices"], produces = ["application/json"])
class DataserviceController(
    private val dataserviceSearchService: DataserviceSearchService
) {
    @PostMapping
    fun searchDataservices(@RequestBody query: SearchOperation): ResponseEntity<List<Dataservice>> =
        ResponseEntity(dataserviceSearchService.searchDataservices(query), HttpStatus.OK)
}
