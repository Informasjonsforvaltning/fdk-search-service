package no.digdir.fdk.searchservice.controller

import no.digdir.fdk.searchservice.model.Event
import no.digdir.fdk.searchservice.model.SearchOperation
import no.digdir.fdk.searchservice.service.EventSearchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value=["/events"], produces = ["application/json"])
class EventController(
    private val eventSearchService: EventSearchService
) {
    @PostMapping
    fun searchEvents(@RequestBody query: SearchOperation): ResponseEntity<List<Event>> {
        val events: List<Event> = eventSearchService.searchEvents(query)
        return ResponseEntity(events, HttpStatus.OK)
    }
}
