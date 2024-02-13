package no.digdir.fdk.searchservice.controller

import no.digdir.fdk.searchservice.model.InformationModel
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.service.InformationModelSearchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value=["/informationmodels"], produces = ["application/json"])
class InformationModelController(
    private val informationModelSearchService: InformationModelSearchService
) {
    @PostMapping
    fun searchInformationModels(@RequestBody query: SearchOperation): ResponseEntity<List<InformationModel>> {
        val informationModels: List<InformationModel> = informationModelSearchService.searchInformationModels(query)
        return ResponseEntity(informationModels, HttpStatus.OK)
    }
}
