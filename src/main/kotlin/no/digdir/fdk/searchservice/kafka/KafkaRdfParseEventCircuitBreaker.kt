package no.digdir.fdk.searchservice.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.micrometer.core.instrument.Metrics
import no.digdir.fdk.searchservice.elastic.SearchRepository
import no.digdir.fdk.searchservice.mapper.toSearchObject
import no.digdir.fdk.searchservice.model.*
import no.fdk.rdf.parse.RdfParseResourceType
import org.apache.avro.generic.GenericRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.time.measureTimedValue
import kotlin.time.toJavaDuration

@Component
open class KafkaRdfParseEventCircuitBreaker(
    private val harvestEventProducer: HarvestEventProducer
) {

    private fun GenericRecord.getResourceType(): RdfParseResourceType? {
        val sym = get("resourceType")?.toString() ?: return null
        return try {
            RdfParseResourceType.valueOf(sym)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun GenericRecord.getFdkId(): String = get("fdkId")?.toString() ?: ""
    private fun GenericRecord.getData(): String = get("data")?.toString() ?: ""
    private fun GenericRecord.getTimestamp(): Long = (get("timestamp") as? Number)?.toLong() ?: 0L
    private fun GenericRecord.getHarvestRunId(): String? = safeGetString("harvestRunId")
    private fun GenericRecord.getUri(): String? = safeGetString("uri")

    private fun <T> index(
        event: GenericRecord,
        searchRepository: SearchRepository,
        clazz: Class<T>,
        index: (T) -> Unit
    ) {
        val fdkId = event.getFdkId()
        val timestamp = event.getTimestamp()
        val search = searchRepository.findById(fdkId)
        if (search.isEmpty || (search.get().metadata?.timestamp ?: 0) < timestamp) {
            val payload = jacksonObjectMapper().readValue(event.getData(), clazz)
            index(payload)
        }
    }

    @CircuitBreaker(name = "rdf-parse")
    open fun process(
        event: GenericRecord?,
        searchRepository: SearchRepository
    ) {
        if (event == null) return
        LOGGER.debug("CB Received message")

        val harvestRunId = event.getHarvestRunId()
        val uri = event.getUri()
        val resourceType = event.getResourceType()
        val startTime = Instant.now()
        var resourceUri: String? = null

        try {
            val timeElapsed = measureTimedValue {
                when (resourceType) {
                    RdfParseResourceType.DATASET -> {
                        LOGGER.debug("Index dataset - id: " + event.getFdkId())
                        index(event, searchRepository, Dataset::class.java) {
                            val searchObject = it.toSearchObject(event.getFdkId(), event.getTimestamp())
                            resourceUri = searchObject.uri ?: uri
                            if (searchObject.uri == null) LOGGER.warn("No uri found for ${event.getFdkId()}")
                            searchRepository.save(searchObject)
                        }
                    }
                    RdfParseResourceType.DATA_SERVICE -> {
                        LOGGER.debug("Index dataservice - id: " + event.getFdkId())
                        index(event, searchRepository, DataService::class.java) {
                            val searchObject = it.toSearchObject(event.getFdkId(), event.getTimestamp())
                            resourceUri = searchObject.uri ?: uri
                            if (searchObject.uri == null) LOGGER.warn("No uri found for ${event.getFdkId()}")
                            searchRepository.save(searchObject)
                        }
                    }
                    RdfParseResourceType.CONCEPT -> {
                        LOGGER.debug("Index concept - id: " + event.getFdkId())
                        index(event, searchRepository, Concept::class.java) {
                            val searchObject = it.toSearchObject(event.getFdkId(), event.getTimestamp())
                            resourceUri = searchObject.uri ?: uri
                            if (searchObject.uri == null) LOGGER.warn("No uri found for ${event.getFdkId()}")
                            searchRepository.save(searchObject)
                        }
                    }
                    RdfParseResourceType.INFORMATION_MODEL -> {
                        LOGGER.debug("Index informationmodel - id: " + event.getFdkId())
                        index(event, searchRepository, InformationModel::class.java) {
                            val searchObject = it.toSearchObject(event.getFdkId(), event.getTimestamp())
                            resourceUri = searchObject.uri ?: uri
                            if (searchObject.uri == null) LOGGER.warn("No uri found for ${event.getFdkId()}")
                            searchRepository.save(searchObject)
                        }
                    }
                    RdfParseResourceType.EVENT -> {
                        LOGGER.debug("Index event - id: " + event.getFdkId())
                        index(event, searchRepository, Event::class.java) {
                            val searchObject = it.toSearchObject(event.getFdkId(), event.getTimestamp())
                            resourceUri = searchObject.uri ?: uri
                            if (searchObject.uri == null) LOGGER.warn("No uri found for ${event.getFdkId()}")
                            searchRepository.save(searchObject)
                        }
                    }
                    RdfParseResourceType.SERVICE -> {
                        LOGGER.debug("Index service - id: " + event.getFdkId())
                        index(event, searchRepository, Service::class.java) {
                            val searchObject = it.toSearchObject(event.getFdkId(), event.getTimestamp())
                            resourceUri = searchObject.uri ?: uri
                            if (searchObject.uri == null) LOGGER.warn("No uri found for ${event.getFdkId()}")
                            searchRepository.save(searchObject)
                        }
                    }
                    null -> { /* unknown resource type, skip */ }
                }
            }
            if (resourceType != null) {
                Metrics.timer("search_index", "type", resourceType.name.lowercase())
                    .record(timeElapsed.duration.toJavaDuration())

                val endTime = Instant.now()
                harvestEventProducer.produceSearchProcessingEvent(
                    harvestRunId = harvestRunId,
                    resourceType = resourceType,
                    fdkId = event.getFdkId(),
                    resourceUri = resourceUri,
                    startTime = startTime,
                    endTime = endTime,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            LOGGER.error("Error processing message: " + e.message)
            if (resourceType != null) {
                Metrics.counter(
                    "search_index_error",
                    "type", resourceType.name.lowercase()
                ).increment()

                val endTime = Instant.now()
                harvestEventProducer.produceSearchProcessingEvent(
                    harvestRunId = harvestRunId,
                    resourceType = resourceType,
                    fdkId = event.getFdkId(),
                    resourceUri = uri,
                    startTime = startTime,
                    endTime = endTime,
                    errorMessage = e.message
                )
            }

            throw e
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(KafkaRdfParseEventCircuitBreaker::class.java)
    }
}
