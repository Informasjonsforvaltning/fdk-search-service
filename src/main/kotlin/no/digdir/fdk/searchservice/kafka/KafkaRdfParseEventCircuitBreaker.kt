package no.digdir.fdk.searchservice.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.micrometer.core.instrument.Metrics
import no.digdir.fdk.searchservice.elastic.SearchRepository
import no.digdir.fdk.searchservice.mapper.toSearchObject
import no.digdir.fdk.searchservice.model.Concept
import no.digdir.fdk.searchservice.model.DataService
import no.digdir.fdk.searchservice.model.Dataset
import no.digdir.fdk.searchservice.model.Event
import no.digdir.fdk.searchservice.model.InformationModel
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.Service
import no.fdk.rdf.parse.RdfParseResourceType
import org.apache.avro.generic.GenericRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.time.measureTimedValue
import kotlin.time.toJavaDuration

@Component
class KafkaRdfParseEventCircuitBreaker(
    private val harvestEventProducer: HarvestEventProducer,
    circuitBreakerRegistry: CircuitBreakerRegistry,
) {
    private val circuitBreaker = circuitBreakerRegistry.circuitBreaker("rdf-parse")

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

    /**
     * Deserializes and saves [event] as a [clazz] search object, unless a fresher version is
     * already indexed. Returns the resulting URI (falling back to [fallbackUri]), or `null` if
     * the event was stale and nothing was indexed.
     */
    private fun <T> indexIfNewer(
        event: GenericRecord,
        searchRepository: SearchRepository,
        clazz: Class<T>,
        resourceTypeLabel: String,
        fallbackUri: String?,
        toSearchObject: (T) -> SearchObject,
    ): String? {
        LOGGER.debug("Index $resourceTypeLabel - id: " + event.getFdkId())

        val fdkId = event.getFdkId()
        val timestamp = event.getTimestamp()
        val search = searchRepository.findById(fdkId)
        if (search.isEmpty || (search.get().metadata?.timestamp ?: 0) < timestamp) {
            val payload = jacksonObjectMapper().readValue(event.getData(), clazz)
            val searchObject = toSearchObject(payload)
            if (searchObject.uri == null) LOGGER.warn("No uri found for $fdkId")
            searchRepository.save(searchObject)
            return searchObject.uri ?: fallbackUri
        }
        return null
    }

    fun process(
        event: GenericRecord?,
        searchRepository: SearchRepository,
    ) {
        if (event == null) return
        circuitBreaker.executeRunnable {
            processInternal(event, searchRepository)
        }
    }

    private fun processInternal(
        event: GenericRecord,
        searchRepository: SearchRepository,
    ) {
        LOGGER.debug("CB Received message")

        val harvestRunId = event.getHarvestRunId()
        val uri = event.getUri()
        val resourceType = event.getResourceType()
        val startTime = Instant.now()
        var resourceUri: String? = null

        try {
            val timeElapsed =
                measureTimedValue {
                    resourceUri =
                        when (resourceType) {
                            RdfParseResourceType.DATASET -> {
                                indexIfNewer(event, searchRepository, Dataset::class.java, "dataset", uri) {
                                    it.toSearchObject(event.getFdkId(), event.getTimestamp())
                                }
                            }

                            RdfParseResourceType.DATA_SERVICE -> {
                                indexIfNewer(event, searchRepository, DataService::class.java, "dataservice", uri) {
                                    it.toSearchObject(event.getFdkId(), event.getTimestamp())
                                }
                            }

                            RdfParseResourceType.CONCEPT -> {
                                indexIfNewer(event, searchRepository, Concept::class.java, "concept", uri) {
                                    it.toSearchObject(event.getFdkId(), event.getTimestamp())
                                }
                            }

                            RdfParseResourceType.INFORMATION_MODEL -> {
                                indexIfNewer(event, searchRepository, InformationModel::class.java, "informationmodel", uri) {
                                    it.toSearchObject(event.getFdkId(), event.getTimestamp())
                                }
                            }

                            RdfParseResourceType.EVENT -> {
                                indexIfNewer(event, searchRepository, Event::class.java, "event", uri) {
                                    it.toSearchObject(event.getFdkId(), event.getTimestamp())
                                }
                            }

                            RdfParseResourceType.SERVICE -> {
                                indexIfNewer(event, searchRepository, Service::class.java, "service", uri) {
                                    it.toSearchObject(event.getFdkId(), event.getTimestamp())
                                }
                            }

                            null -> {
                                null
                            }
                        }
                }
            if (resourceType != null) {
                Metrics
                    .timer("search_index", "type", resourceType.name.lowercase())
                    .record(timeElapsed.duration.toJavaDuration())

                val endTime = Instant.now()
                harvestEventProducer.produceSearchProcessingEvent(
                    harvestRunId = harvestRunId,
                    resourceType = resourceType,
                    fdkId = event.getFdkId(),
                    resourceUri = resourceUri,
                    startTime = startTime,
                    endTime = endTime,
                    errorMessage = null,
                )
            }
        } catch (e: Exception) {
            LOGGER.error("Error processing message: " + e.message)
            if (resourceType != null) {
                Metrics
                    .counter(
                        "search_index_error",
                        "type",
                        resourceType.name.lowercase(),
                    ).increment()

                val endTime = Instant.now()
                harvestEventProducer.produceSearchProcessingEvent(
                    harvestRunId = harvestRunId,
                    resourceType = resourceType,
                    fdkId = event.getFdkId(),
                    resourceUri = uri,
                    startTime = startTime,
                    endTime = endTime,
                    errorMessage = e.message,
                )
            }

            throw e
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(KafkaRdfParseEventCircuitBreaker::class.java)
    }
}
