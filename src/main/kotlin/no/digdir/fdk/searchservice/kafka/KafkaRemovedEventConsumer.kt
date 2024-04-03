package no.digdir.fdk.searchservice.kafka

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
import no.fdk.informationmodels.InformationModelEvent
import no.fdk.informationmodels.InformationModelEventType
import no.fdk.service.ServiceEvent
import no.fdk.service.ServiceEventType
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component


@Component
class KafkaRemovedEventConsumer(
    private val searchRepository: SearchRepository
) {
    @KafkaListener(
        topics = [
            "dataset-events",
            "data-service-events",
            "concept-events",
            "information-model-events",
            "event-events",
            "service-events"],
        groupId = "fdk-search-service",
        concurrency = "4",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(record: ConsumerRecord<String, SpecificRecord>, ack: Acknowledgment) {
        LOGGER.debug("Received message - offset: " + record.offset())

        val event = record.value()
        try {
            event.let {
                if (it is DatasetEvent && it.type == DatasetEventType.DATASET_REMOVED) {
                    LOGGER.debug("Remove dataset - id: " + it.fdkId)
                    searchRepository.markDeletedIfTimestampIsNewer("${it.fdkId}", it.timestamp, SearchType.DATASET)
                } else if (it is DataServiceEvent && it.type == DataServiceEventType.DATA_SERVICE_REMOVED) {
                    LOGGER.debug("Remove data-service - id: " + it.fdkId)
                    searchRepository.markDeletedIfTimestampIsNewer("${it.fdkId}", it.timestamp, SearchType.DATA_SERVICE)
                } else if (it is ConceptEvent && it.type == ConceptEventType.CONCEPT_REMOVED) {
                    LOGGER.debug("Remove concept - id: " + it.fdkId)
                    searchRepository.markDeletedIfTimestampIsNewer("${it.fdkId}", it.timestamp, SearchType.CONCEPT)
                } else if (it is InformationModelEvent && it.type == InformationModelEventType.INFORMATION_MODEL_REMOVED) {
                    LOGGER.debug("Remove information-model - id: " + it.fdkId)
                    searchRepository.markDeletedIfTimestampIsNewer("${it.fdkId}", it.timestamp, SearchType.INFORMATION_MODEL)
                } else if (it is ServiceEvent && it.type == ServiceEventType.SERVICE_REMOVED) {
                    LOGGER.debug("Remove service - id: " + it.fdkId)
                    searchRepository.markDeletedIfTimestampIsNewer("${it.fdkId}", it.timestamp, SearchType.SERVICE)
                } else if (it is EventEvent && it.type == EventEventType.EVENT_REMOVED) {
                    LOGGER.debug("Remove event - id: " + it.fdkId)
                    searchRepository.markDeletedIfTimestampIsNewer("${it.fdkId}", it.timestamp, SearchType.EVENT)
                }
            }
            ack.acknowledge()
        } catch (e: Exception) {
            LOGGER.error("Error processing message: " + e.message)
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
                    timestamp = timestamp),
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
                isRelatedToTransportportal = null
            ).let { save(it) }
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(KafkaRemovedEventConsumer::class.java)
    }
}
