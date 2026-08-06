package no.digdir.fdk.searchservice.unit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.digdir.fdk.searchservice.data.TEST_NULL_DATASET
import no.digdir.fdk.searchservice.data.TEST_NULL_EVENT
import no.digdir.fdk.searchservice.data.TEST_NULL_SERVICE
import no.digdir.fdk.searchservice.mapper.toSearchObject
import no.digdir.fdk.searchservice.model.SpecializedType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Nested
    internal inner class Service {
        @Test
        fun `public service to search object has correct specialized type`() {
            val publicService = TEST_NULL_SERVICE.copy(uri = "test", specializedType = "publicService")
            assertEquals(SpecializedType.PUBLIC_SERVICE, publicService.toSearchObject("test", 0).specializedType)
        }

        @Test
        fun `generic service to search object has correct specialized type`() {
            val genericService = TEST_NULL_SERVICE.copy(uri = "test", specializedType = "service")
            assertEquals(SpecializedType.SERVICE, genericService.toSearchObject("test", 0).specializedType)
        }

        @Test
        fun `public service serializes to camelCase JSON`() {
            val searchObject = TEST_NULL_SERVICE.copy(uri = "test", specializedType = "publicService").toSearchObject("test", 0)
            val json = jacksonObjectMapper().writeValueAsString(searchObject)
            assertTrue(json.contains("\"specializedType\":\"publicService\""))
        }

        @Test
        fun `generic service serializes to camelCase JSON`() {
            val searchObject = TEST_NULL_SERVICE.copy(uri = "test", specializedType = "service").toSearchObject("test", 0)
            val json = jacksonObjectMapper().writeValueAsString(searchObject)
            assertTrue(json.contains("\"specializedType\":\"service\""))
        }
    }
}
