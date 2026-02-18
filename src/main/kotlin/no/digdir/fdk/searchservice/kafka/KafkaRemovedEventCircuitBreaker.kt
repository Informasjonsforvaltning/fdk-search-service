package no.digdir.fdk.searchservice.kafka

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.micrometer.core.instrument.Metrics
import no.digdir.fdk.searchservice.elastic.SearchRepository
import no.digdir.fdk.searchservice.model.Metadata
import no.digdir.fdk.searchservice.model.SearchObject
import no.digdir.fdk.searchservice.model.SearchType
import no.fdk.rdf.parse.RdfParseResourceType
import org.apache.avro.generic.GenericRecord
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

    private fun GenericRecord.getTypeSymbol(): String? =
        (get("type")?.toString())?.takeIf { it.isNotBlank() }

    private fun GenericRecord.getHarvestRunId(): String? =
        get("harvestRunId")?.toString()?.takeIf { it.isNotBlank() }

    private fun GenericRecord.getUri(): String? =
        get("uri")?.toString()?.takeIf { it.isNotBlank() }

    private fun GenericRecord.getFdkId(): String? =
        get("fdkId")?.toString()?.takeIf { it.isNotBlank() }

    private fun GenericRecord.getTimestamp(): Long =
        (get("timestamp") as? Number)?.toLong() ?: 0L

    private fun GenericRecord.getResourceTypeName(): String =
        when (getTypeSymbol()) {
            "DATASET_REMOVED", "DATASET_HARVESTED", "DATASET_REASONED" -> "dataset"
            "DATA_SERVICE_REMOVED", "DATA_SERVICE_HARVESTED", "DATA_SERVICE_REASONED" -> "data-service"
            "CONCEPT_REMOVED", "CONCEPT_HARVESTED", "CONCEPT_REASONED" -> "concept"
            "INFORMATION_MODEL_REMOVED", "INFORMATION_MODEL_HARVESTED", "INFORMATION_MODEL_REASONED" -> "information-model"
            "SERVICE_REMOVED", "SERVICE_HARVESTED", "SERVICE_REASONED" -> "service"
            "EVENT_REMOVED", "EVENT_HARVESTED", "EVENT_REASONED" -> "event"
            else -> "invalid-type"
        }

    private fun GenericRecord.getRdfParseResourceType(): RdfParseResourceType? =
        when (getTypeSymbol()) {
            "DATASET_REMOVED", "DATASET_HARVESTED", "DATASET_REASONED" -> RdfParseResourceType.DATASET
            "DATA_SERVICE_REMOVED", "DATA_SERVICE_HARVESTED", "DATA_SERVICE_REASONED" -> RdfParseResourceType.DATA_SERVICE
            "CONCEPT_REMOVED", "CONCEPT_HARVESTED", "CONCEPT_REASONED" -> RdfParseResourceType.CONCEPT
            "INFORMATION_MODEL_REMOVED", "INFORMATION_MODEL_HARVESTED", "INFORMATION_MODEL_REASONED" -> RdfParseResourceType.INFORMATION_MODEL
            "SERVICE_REMOVED", "SERVICE_HARVESTED", "SERVICE_REASONED" -> RdfParseResourceType.SERVICE
            "EVENT_REMOVED", "EVENT_HARVESTED", "EVENT_REASONED" -> RdfParseResourceType.EVENT
            else -> null
        }

    private fun GenericRecord.getSearchType(): SearchType? =
        when (getTypeSymbol()) {
            "DATASET_REMOVED" -> SearchType.DATASET
            "DATA_SERVICE_REMOVED" -> SearchType.DATA_SERVICE
            "CONCEPT_REMOVED" -> SearchType.CONCEPT
            "INFORMATION_MODEL_REMOVED" -> SearchType.INFORMATION_MODEL
            "SERVICE_REMOVED" -> SearchType.SERVICE
            "EVENT_REMOVED" -> SearchType.EVENT
            else -> null
        }

    @CircuitBreaker(name = "remove")
    open fun process(record: ConsumerRecord<String, GenericRecord>) {
        LOGGER.debug("Received message - offset: " + record.offset())

        val event = record.value() ?: return
        val harvestRunId = event.getHarvestRunId()
        val eventUri = event.getUri()
        val resourceType = event.getRdfParseResourceType()
        val searchType = event.getSearchType()
        val startTime = Instant.now()
        var resourceUri: String? = null
        var fdkId: String? = event.getFdkId()

        try {
            val (deleted, timeElapsed) = measureTimedValue {
                if (searchType != null && fdkId != null) {
                    LOGGER.debug("Remove {} - id: {}", event.getResourceTypeName(), fdkId)
                    resourceUri = searchRepository.findByIdOrNull(fdkId)?.uri ?: eventUri
                    searchRepository.markDeletedIfTimestampIsNewer(
                        fdkId,
                        event.getTimestamp(),
                        searchType
                    )
                    true
                } else {
                    LOGGER.debug("Unknown event type: {}, skipping", event.getTypeSymbol())
                    false
                }
            }

            if (deleted) {
                Metrics.timer("search_delete", "type", event.getResourceTypeName())
                    .record(timeElapsed.toJavaDuration())

                if (harvestRunId != null && fdkId != null && resourceType != null) {
                    val endTime = Instant.now()
                    harvestEventProducer.produceSearchProcessingEvent(
                        harvestRunId = harvestRunId,
                        resourceType = resourceType,
                        fdkId = fdkId,
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
                "type", event.getResourceTypeName()
            ).increment()

            if (harvestRunId != null && fdkId != null && resourceType != null) {
                val endTime = Instant.now()
                harvestEventProducer.produceSearchProcessingEvent(
                    harvestRunId = harvestRunId,
                    resourceType = resourceType,
                    fdkId = fdkId,
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
