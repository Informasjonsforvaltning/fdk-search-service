package no.digdir.fdk.searchservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController {
    @GetMapping("/search")
    fun search(): ResponseEntity<Void> =
        // TODO: Implement search
        ResponseEntity.ok().build()
}
