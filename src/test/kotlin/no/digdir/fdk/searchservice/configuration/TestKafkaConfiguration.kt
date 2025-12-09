package no.digdir.fdk.searchservice.configuration

import no.fdk.harvest.HarvestEvent
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.util.concurrent.CompletableFuture

@TestConfiguration
class TestKafkaConfiguration {
    @Bean
    @Primary
    fun kafkaTemplate(): KafkaTemplate<String, HarvestEvent> {
        // Return a mock KafkaTemplate for tests
        // This prevents Spring Boot from trying to create a real KafkaTemplate
        // which requires Kafka and Schema Registry to be available
        val template = mock<KafkaTemplate<String, HarvestEvent>>()
        whenever(template.send(any(), any(), any())).thenReturn(
            CompletableFuture.completedFuture(mock<SendResult<String, HarvestEvent>>())
        )
        return template
    }
}
