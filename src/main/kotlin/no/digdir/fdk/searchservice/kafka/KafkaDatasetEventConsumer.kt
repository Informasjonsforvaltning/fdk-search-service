package no.digdir.fdk.searchservice.kafka;

import no.fdk.rdf.parse.RdfParseEvent
import no.fdk.rdf.parse.RdfParseResourceType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component


@Component
class KafkaDatasetEventConsumer {

    @KafkaListener(
        topics = ["rdf-parse-events"],
        groupId = "fdk-search-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(record: ConsumerRecord<String, RdfParseEvent>, ack: Acknowledgment) {
        LOGGER.debug("Received message - offset: " + record.offset())

        val event = record.value();
        try {
            // TODO: Implement logic for handling RDF parse event

            if(event?.resourceType == RdfParseResourceType.DATASET) {
                LOGGER.debug("Index dataset - id: " + event.fdkId)
            } else if(event?.resourceType == RdfParseResourceType.DATASERVICE) {
                LOGGER.debug("Index dataservice - id: " + event.fdkId)
            }
            // ...
            ack.acknowledge();
        } catch (e: Exception) {
            LOGGER.error("Error processing message: " + e.message)
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(KafkaDatasetEventConsumer::class.java)
    }
}