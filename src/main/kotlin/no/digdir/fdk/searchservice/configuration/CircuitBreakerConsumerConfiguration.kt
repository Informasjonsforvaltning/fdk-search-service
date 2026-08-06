package no.digdir.fdk.searchservice.configuration

import io.github.resilience4j.circuitbreaker.CircuitBreaker.StateTransition
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent
import no.digdir.fdk.searchservice.kafka.KafkaManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration

@Configuration
class CircuitBreakerConsumerConfiguration(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val kafkaManager: KafkaManager,
) {
    init {
        LOGGER.debug("Configuring circuit breaker event listener")
        registerPauseResumeListener("rdf-parse", lagDescription = "rdf-parse-events lag will grow until it closes")
        registerPauseResumeListener("remove", lagDescription = "dataset/removed-event lag will grow until it closes")
    }

    private fun registerPauseResumeListener(
        breakerName: String,
        lagDescription: String,
    ) {
        circuitBreakerRegistry.circuitBreaker(breakerName).eventPublisher.onStateTransition { event: CircuitBreakerOnStateTransitionEvent ->
            when (event.stateTransition) {
                StateTransition.CLOSED_TO_OPEN,
                StateTransition.CLOSED_TO_FORCED_OPEN,
                StateTransition.HALF_OPEN_TO_OPEN,
                -> {
                    LOGGER.warn(
                        "Circuit breaker '{}' opened ({}); pausing Kafka listener - {}",
                        breakerName,
                        event.stateTransition,
                        lagDescription,
                    )
                    kafkaManager.pause(breakerName)
                }

                StateTransition.OPEN_TO_HALF_OPEN,
                StateTransition.HALF_OPEN_TO_CLOSED,
                StateTransition.FORCED_OPEN_TO_CLOSED,
                StateTransition.FORCED_OPEN_TO_HALF_OPEN,
                -> {
                    LOGGER.warn("Circuit breaker '{}' closed; resuming Kafka listener - consumption will resume", breakerName)
                    kafkaManager.resume(breakerName)
                }

                else -> {
                    throw IllegalStateException("Unknown transition state: " + event.stateTransition)
                }
            }
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CircuitBreakerConsumerConfiguration::class.java)
    }
}
