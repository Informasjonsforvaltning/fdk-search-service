package no.digdir.fdk.searchservice.controller

import no.digdir.fdk.searchservice.model.Dataset
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.service.DatasetSearchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value=["/datasets"], produces = ["application/json"])
class DatasetsController(
    private val datasetSearchService: DatasetSearchService
) {
    @PostMapping
    fun searchDatasets(@RequestBody query: SearchOperation): ResponseEntity<List<Dataset>> {
        val datasets: List<Dataset> = datasetSearchService.searchDatasets(query)
        return ResponseEntity(datasets, HttpStatus.OK)
    }
}
