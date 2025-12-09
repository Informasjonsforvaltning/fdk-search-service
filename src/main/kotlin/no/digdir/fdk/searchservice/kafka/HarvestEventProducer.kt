package no.digdir.fdk.searchservice.kafka

import no.fdk.harvest.HarvestEvent
import no.fdk.harvest.HarvestPhase
import no.fdk.harvest.DataType
import no.fdk.rdf.parse.RdfParseResourceType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class HarvestEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, HarvestEvent>
) {
    fun produceSearchProcessingEvent(
        harvestRunId: String?,
        resourceType: RdfParseResourceType,
        fdkId: String,
        resourceUri: String?,
        startTime: Instant,
        endTime: Instant,
        errorMessage: String? = null
    ) {
        if (harvestRunId == null) {
            LOGGER.debug("Skipping harvest event production - no harvestRunId provided for fdkId: $fdkId")
            return
        }

        val dataType = mapResourceTypeToDataType(resourceType)
        val harvestEvent = HarvestEvent.newBuilder()
            .setPhase(HarvestPhase.SEARCH_PROCESSING)
            .setRunId(harvestRunId)
            .setDataType(dataType)
            .setFdkId(fdkId)
            .setResourceUri(resourceUri)
            .setTimestamp(endTime.toEpochMilli())
            .setStartTime(startTime.toString())
            .setEndTime(endTime.toString())
            .setErrorMessage(errorMessage)
            .setDataSourceId(null)
            .setDataSourceUrl(null)
            .setAcceptHeader(null)
            .setChangedResourcesCount(null)
            .setUnchangedResourcesCount(null)
            .setRemovedResourcesCount(null)
            .build()

        try {
            kafkaTemplate.send("harvest-events", harvestRunId, harvestEvent)
            LOGGER.debug("Produced harvest event for fdkId: $fdkId, harvestRunId: $harvestRunId, phase: SEARCH_PROCESSING")
        } catch (e: Exception) {
            LOGGER.error("Failed to produce harvest event for fdkId: $fdkId, harvestRunId: $harvestRunId", e)
            // Don't throw - we don't want to fail the indexing if event production fails
        }
    }

    private fun mapResourceTypeToDataType(resourceType: RdfParseResourceType): DataType {
        return when (resourceType) {
            RdfParseResourceType.DATASET -> DataType.dataset
            RdfParseResourceType.DATA_SERVICE -> DataType.dataservice
            RdfParseResourceType.CONCEPT -> DataType.concept
            RdfParseResourceType.INFORMATION_MODEL -> DataType.informationmodel
            RdfParseResourceType.SERVICE -> DataType.publicService
            RdfParseResourceType.EVENT -> DataType.event
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(HarvestEventProducer::class.java)
    }
}
