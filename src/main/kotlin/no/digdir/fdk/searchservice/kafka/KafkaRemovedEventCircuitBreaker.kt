package no.digdir.fdk.searchservice.kafka

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.micrometer.core.instrument.Metrics
import no.digdir.fdk.searchservice.elastic.SearchRepository
import no.digdir.fdk.searchservice.model.Metadata
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType
import no.fdk.concept.ConceptEvent
import no.fdk.concept.ConceptEventType
import no.fdk.dataservice.DataServiceEvent
import no.fdk.dataservice.DataServiceEventType
import no.fdk.dataset.DatasetEvent
import no.fdk.dataset.DatasetEventType
import no.fdk.event.EventEvent
import no.fdk.event.EventEventType
import no.fdk.informationmodel.InformationModelEvent
import no.fdk.informationmodel.InformationModelEventType
import no.fdk.rdf.parse.RdfParseResourceType
import no.fdk.service.ServiceEvent
import no.fdk.service.ServiceEventType
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.time.measureTimedValue
import kotlin.time.toJavaDuration

@Component
open class KafkaRemovedEventCircuitBreaker(
    private val searchRepository: SearchRepository,
    private val harvestEventProducer: HarvestEventProducer
) {
    private fun SpecificRecord.getResourceType(): String {
        return when (this) {
            is DatasetEvent -> "dataset"
            is DataServiceEvent -> "data-service"
            is ConceptEvent -> "concept"
            is InformationModelEvent -> "information-model"
            is ServiceEvent -> "service"
            is EventEvent -> "event"
            else -> "invalid-type"
        }
    }

    private fun SpecificRecord.getRdfParseResourceType(): RdfParseResourceType? {
        return when (this) {
            is DatasetEvent -> RdfParseResourceType.DATASET
            is DataServiceEvent -> RdfParseResourceType.DATA_SERVICE
            is ConceptEvent -> RdfParseResourceType.CONCEPT
            is InformationModelEvent -> RdfParseResourceType.INFORMATION_MODEL
            is ServiceEvent -> RdfParseResourceType.SERVICE
            is EventEvent -> RdfParseResourceType.EVENT
            else -> null
        }
    }

    private fun SpecificRecord.getHarvestRunId(): String? {
        return when (this) {
            is DatasetEvent -> this.harvestRunId?.toString()
            is DataServiceEvent -> this.harvestRunId?.toString()
            is ConceptEvent -> this.harvestRunId?.toString()
            is InformationModelEvent -> this.harvestRunId?.toString()
            is ServiceEvent -> this.harvestRunId?.toString()
            is EventEvent -> this.harvestRunId?.toString()
            else -> null
        }
    }

    private fun SpecificRecord.getUri(): String? {
        return when (this) {
            is DatasetEvent -> this.uri?.toString()
            is DataServiceEvent -> this.uri?.toString()
            is ConceptEvent -> this.uri?.toString()
            is InformationModelEvent -> this.uri?.toString()
            is ServiceEvent -> this.uri?.toString()
            is EventEvent -> this.uri?.toString()
            else -> null
        }
    }

    @CircuitBreaker(name = "remove")
    open fun process(record: ConsumerRecord<String, SpecificRecord>) {
        LOGGER.debug("Received message - offset: " + record.offset())

        val event = record.value()
        val harvestRunId = event.getHarvestRunId()
        val eventUri = event.getUri()
        val resourceType = event.getRdfParseResourceType()
        val startTime = Instant.now()
        var resourceUri: String? = null
        var fdkId: String? = null

        try {
            val (deleted, timeElapsed) = measureTimedValue {
                if (event is DatasetEvent && event.type == DatasetEventType.DATASET_REMOVED) {
                    LOGGER.debug("Remove dataset - id: {}", event.fdkId)
                    fdkId = "${event.fdkId}"
                    resourceUri = searchRepository.findByIdOrNull(fdkId!!)?.uri ?: eventUri
                    searchRepository.markDeletedIfTimestampIsNewer(
                        fdkId!!,
                        event.timestamp,
                        SearchType.DATASET
                    )
                    true
                } else if (event is DataServiceEvent && event.type == DataServiceEventType.DATA_SERVICE_REMOVED) {
                    LOGGER.debug("Remove data-service - id: {}", event.fdkId)
                    fdkId = "${event.fdkId}"
                    resourceUri = searchRepository.findByIdOrNull(fdkId!!)?.uri ?: eventUri
                    searchRepository.markDeletedIfTimestampIsNewer(
                        fdkId!!,
                        event.timestamp,
                        SearchType.DATA_SERVICE
                    )
                    true
                } else if (event is ConceptEvent && event.type == ConceptEventType.CONCEPT_REMOVED) {
                    LOGGER.debug("Remove concept - id: {}", event.fdkId)
                    fdkId = "${event.fdkId}"
                    resourceUri = searchRepository.findByIdOrNull(fdkId!!)?.uri ?: eventUri
                    searchRepository.markDeletedIfTimestampIsNewer(
                        fdkId!!,
                        event.timestamp,
                        SearchType.CONCEPT
                    )
                    true
                } else if (event is InformationModelEvent && event.type == InformationModelEventType.INFORMATION_MODEL_REMOVED) {
                    LOGGER.debug("Remove information-model - id: {}", event.fdkId)
                    fdkId = "${event.fdkId}"
                    resourceUri = searchRepository.findByIdOrNull(fdkId!!)?.uri ?: eventUri
                    searchRepository.markDeletedIfTimestampIsNewer(
                        fdkId!!,
                        event.timestamp,
                        SearchType.INFORMATION_MODEL
                    )
                    true
                } else if (event is ServiceEvent && event.type == ServiceEventType.SERVICE_REMOVED) {
                    LOGGER.debug("Remove service - id: {}", event.fdkId)
                    fdkId = "${event.fdkId}"
                    resourceUri = searchRepository.findByIdOrNull(fdkId!!)?.uri ?: eventUri
                    searchRepository.markDeletedIfTimestampIsNewer(
                        fdkId!!,
                        event.timestamp,
                        SearchType.SERVICE
                    )
                    true
                } else if (event is EventEvent && event.type == EventEventType.EVENT_REMOVED) {
                    LOGGER.debug("Remove event - id: {}", event.fdkId)
                    fdkId = "${event.fdkId}"
                    resourceUri = searchRepository.findByIdOrNull(fdkId!!)?.uri ?: eventUri
                    searchRepository.markDeletedIfTimestampIsNewer(fdkId!!, event.timestamp, SearchType.EVENT)
                    true
                } else {
                    LOGGER.debug("Unknown event type: {}, skipping", event)
                    false
                }
            }

            if (deleted) {
                Metrics.timer("search_delete", "type", event.getResourceType())
                    .record(timeElapsed.toJavaDuration())
                
                // Produce harvest event on success
                if (harvestRunId != null && fdkId != null && resourceType != null) {
                    val endTime = Instant.now()
                    harvestEventProducer.produceSearchProcessingEvent(
                        harvestRunId = harvestRunId,
                        resourceType = resourceType,
                        fdkId = fdkId!!,
                        resourceUri = resourceUri,
                        startTime = startTime,
                        endTime = endTime,
                        errorMessage = null
                    )
                }
            }
        } catch (e: Exception) {
            LOGGER.error("Error processing message: " + e.message)
            Metrics.counter(
                "search_delete_error",
                "type", event.getResourceType()
            ).increment()
            
            // Produce harvest event on failure
            if (harvestRunId != null && fdkId != null && resourceType != null) {
                val endTime = Instant.now()
                harvestEventProducer.produceSearchProcessingEvent(
                    harvestRunId = harvestRunId,
                    resourceType = resourceType,
                    fdkId = fdkId!!,
                    resourceUri = eventUri,
                    startTime = startTime,
                    endTime = endTime,
                    errorMessage = e.message
                )
            }
            
            throw e
        }
    }

    private fun SearchRepository.markDeletedIfTimestampIsNewer(id: String, timestamp: Long, searchType: SearchType) {
        findByIdOrNull(id)?.let {
            if (it.metadata?.timestamp!! < timestamp) {
                save(it.copy(metadata = it.metadata.copy(deleted = true, timestamp = timestamp)))
            }
        } ?: run {
            SearchObject(
                id = id,
                metadata = Metadata(
                    firstHarvested = null,
                    modified = null,
                    deleted = true,
                    timestamp = timestamp
                ),
                searchType = searchType,
                uri = null,
                accessRights = null,
                catalog = null,
                dataTheme = null,
                description = null,
                fdkFormatPrefixed = null,
                isOpenData = false,
                keyword = null,
                losTheme = null,
                organization = null,
                provenance = null,
                relations = null,
                spatial = null,
                title = null,
                specializedType = null,
                isAuthoritative = null,
                isRelatedToTransportportal = null,
                additionalTitles = null
            ).let { save(it) }
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(KafkaRemovedEventCircuitBreaker::class.java)
    }
}
