package no.digdir.fdk.searchservice.unit

import no.digdir.fdk.searchservice.data.TEST_NULL_DATASET
import no.digdir.fdk.searchservice.data.TEST_NULL_EVENT
import no.digdir.fdk.searchservice.mapper.toSearchObject
import no.digdir.fdk.searchservice.model.SpecializedType
import org.junit.jupiter.api.Nested

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


@Tag("unit")
class SpecializedTypeTest {
    @Nested
    internal inner class Dataset {
        @Test
        fun `dataset series to search object has correct specialized type`() {
            val datasetSeries = TEST_NULL_DATASET.copy(uri = "test", specializedType = "datasetSeries")
            assertEquals(SpecializedType.DATASET_SERIES, datasetSeries.toSearchObject("test", 0).specializedType)
        }
    }

    @Nested
    internal inner class Event {
        @Test
        fun `business event to search object has correct specialized type`() {
            val businessEvent = TEST_NULL_EVENT.copy(uri = "test", specializedType = "business_event")
            assertEquals(SpecializedType.BUSINESS_EVENT, businessEvent.toSearchObject("test", 0).specializedType)
        }

        @Test
        fun `life event to search object has correct specialized type`() {
            val businessEvent = TEST_NULL_EVENT.copy(uri = "test", specializedType = "life_event")
            assertEquals(SpecializedType.LIFE_EVENT, businessEvent.toSearchObject("test", 0).specializedType)
        }
    }
}
