package no.digdir.fdk.searchservice.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.micrometer.core.instrument.Metrics
import no.digdir.fdk.searchservice.elastic.SearchRepository
import no.digdir.fdk.searchservice.mapper.toSearchObject
import no.digdir.fdk.searchservice.model.*
import no.fdk.rdf.parse.RdfParseEvent
import no.fdk.rdf.parse.RdfParseResourceType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.time.measureTimedValue
import kotlin.time.toJavaDuration

@Component
open class KafkaRdfParseEventCircuitBreaker {

    private fun <T> index(
        event: RdfParseEvent,
        searchRepository: SearchRepository,
        clazz: Class<T>,
        index: (T) -> Unit
    ) {
        val search = searchRepository.findById("${event.fdkId}")
        if (search.isEmpty || (search.get().metadata?.timestamp ?: 0) < event.timestamp) {
            val payload = jacksonObjectMapper().readValue(event.data.toString(), clazz)
            index(payload)
        }
    }

    @CircuitBreaker(name = "rdf-parse")
    open fun process(
        record: ConsumerRecord<String, RdfParseEvent>,
        searchRepository: SearchRepository
    ) {
        LOGGER.debug("CB Received message - offset: " + record.offset())

        val event = record.value()
        try {
            val timeElapsed = measureTimedValue {
                if (event?.resourceType == RdfParseResourceType.DATASET) {
                    LOGGER.debug("Index dataset - id: " + event.fdkId)
                    index(event, searchRepository, Dataset::class.java) {
                        searchRepository.save(it.toSearchObject("${event.fdkId}", event.timestamp))
                    }
                } else if (event?.resourceType == RdfParseResourceType.DATA_SERVICE) {
                    LOGGER.debug("Index dataservice - id: " + event.fdkId)
                    index(event, searchRepository, DataService::class.java) {
                        searchRepository.save(it.toSearchObject("${event.fdkId}", event.timestamp))
                    }
                } else if (event?.resourceType == RdfParseResourceType.CONCEPT) {
                    LOGGER.debug("Index concept - id: " + event.fdkId)
                    index(event, searchRepository, Concept::class.java) {
                        searchRepository.save(it.toSearchObject("${event.fdkId}", event.timestamp))
                    }
                } else if (event?.resourceType == RdfParseResourceType.INFORMATION_MODEL) {
                    LOGGER.debug("Index informationmodel - id: " + event.fdkId)
                    index(event, searchRepository, InformationModel::class.java) {
                        searchRepository.save(it.toSearchObject("${event.fdkId}", event.timestamp))
                    }
                } else if (event?.resourceType == RdfParseResourceType.EVENT) {
                    LOGGER.debug("Index event - id: " + event.fdkId)
                    index(event, searchRepository, Event::class.java) {
                        searchRepository.save(it.toSearchObject("${event.fdkId}", event.timestamp))
                    }
                } else if (event?.resourceType == RdfParseResourceType.SERVICE) {
                    LOGGER.debug("Index service - id: " + event.fdkId)
                    index(event, searchRepository, Service::class.java) {
                        searchRepository.save(it.toSearchObject("${event.fdkId}", event.timestamp))
                    }
                }
            }
            Metrics.timer("search_index", "type", event.resourceType.name.lowercase())
                .record(timeElapsed.duration.toJavaDuration())
        } catch (e: Exception) {
            LOGGER.error("Error processing message: " + e.message)
            Metrics.counter(
                "search_index_error",
                "type", event.resourceType.name.lowercase()
            ).increment()
            throw e
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(KafkaRdfParseEventCircuitBreaker::class.java)
    }
}
