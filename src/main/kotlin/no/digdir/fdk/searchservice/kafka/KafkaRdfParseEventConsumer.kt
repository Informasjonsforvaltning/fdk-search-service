package no.digdir.fdk.searchservice.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import no.digdir.fdk.searchservice.elastic.SearchRepository
import no.digdir.fdk.searchservice.mapper.toSearchObject
import no.digdir.fdk.searchservice.model.*
import no.fdk.rdf.parse.RdfParseEvent
import no.fdk.rdf.parse.RdfParseResourceType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component


@Component
class KafkaRdfParseEventConsumer(
    private val searchRepository: SearchRepository
) {
    private fun <T> index(event: RdfParseEvent, clazz: Class<T>, index: (T) -> Unit) {
        val search = searchRepository.findById("${event.fdkId}")
        if (search.isEmpty || (search.get().metadata?.timestamp ?: 0) < event.timestamp) {
            val payload = ObjectMapper().readValue(event.data.toString(), clazz)
            index(payload)
        }
    }

    @KafkaListener(
        topics = ["rdf-parse-events"],
        groupId = "fdk-search-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(record: ConsumerRecord<String, RdfParseEvent>, ack: Acknowledgment) {
        LOGGER.debug("Received message - offset: " + record.offset())

        val event = record.value()
        try {
            if (event?.resourceType == RdfParseResourceType.DATASET) {
                LOGGER.debug("Index dataset - id: " + event.fdkId)
                index(event, Dataset::class.java) {
                    searchRepository.save(it.toSearchObject(event.timestamp))
                }
            } else if (event?.resourceType == RdfParseResourceType.DATASERVICE) {
                LOGGER.debug("Index dataservice - id: " + event.fdkId)
                index(event, DataService::class.java) {
                    searchRepository.save(it.toSearchObject(event.timestamp))
                }
            } else if (event?.resourceType == RdfParseResourceType.CONCEPT) {
                LOGGER.debug("Index concept - id: " + event.fdkId)
                index(event, Concept::class.java) {
                    searchRepository.save(it.toSearchObject(event.timestamp))
                }
            } else if (event?.resourceType == RdfParseResourceType.INFORMATIONMODEL) {
                LOGGER.debug("Index informationmodel - id: " + event.fdkId)
                index(event, InformationModel::class.java) {
                    searchRepository.save(it.toSearchObject(event.timestamp))
                }
            } else if (event?.resourceType == RdfParseResourceType.EVENT) {
                LOGGER.debug("Index event - id: " + event.fdkId)
                index(event, Event::class.java) {
                    searchRepository.save(it.toSearchObject(event.timestamp))
                }
            } else if (event?.resourceType == RdfParseResourceType.SERVICE) {
                LOGGER.debug("Index service - id: " + event.fdkId)
                index(event, Service::class.java) {
                    searchRepository.save(it.toSearchObject(event.timestamp))
                }
            }
            ack.acknowledge()
        } catch (e: Exception) {
            LOGGER.error("Error processing message: " + e.message)
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(KafkaRdfParseEventConsumer::class.java)
    }
}
