package no.digdir.fdk.searchservice.kafka

import no.digdir.fdk.searchservice.elastic.SearchRepository
import no.fdk.rdf.parse.RdfParseEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.time.Duration


@Component
class KafkaRdfParseEventConsumer(
    private val searchRepository: SearchRepository,
    private val kafkaRdfParseEventCircuitBreaker: KafkaRdfParseEventCircuitBreaker
) {

    @KafkaListener(
        topics = ["rdf-parse-events"],
        groupId = "fdk-search-service",
        containerFactory = "kafkaListenerContainerFactory",
        concurrency = "4",
        id = "rdf-parse"
    )
    fun listen(record: ConsumerRecord<String, RdfParseEvent>, ack: Acknowledgment) {
        try {
            kafkaRdfParseEventCircuitBreaker.process(record, searchRepository)
            ack.acknowledge()
        } catch (e: Exception) {
            ack.nack(Duration.ZERO)
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(KafkaRdfParseEventConsumer::class.java)
    }
}
