package no.digdir.fdk.searchservice.unit

import no.digdir.fdk.searchservice.data.TEST_CONCEPT_HIT_ALL_FIELDS
import no.digdir.fdk.searchservice.data.TEST_DATASET_FILTERS
import no.digdir.fdk.searchservice.data.TEST_DATASET_HIT_ALL_FIELDS
import no.digdir.fdk.searchservice.mapper.toSearchObject
import no.digdir.fdk.searchservice.model.LocalizedStrings
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Tag("unit")
class MappingTest {
    @Test
    fun `hiddenTerm and altTerm relations to searchObject additionalTitle`() {
        assertEquals(
            expectedAdditionalTitles,
            TEST_CONCEPT_HIT_ALL_FIELDS.toSearchObject(id = "1234", timestamp = 1727765537686).additionalTitles,
        )
    }

    @Test
    fun `dcatProfiles from the parsed dataset are kept in the search object`() {
        assertEquals(
            setOf("MOBILITY_DCAT_AP"),
            TEST_DATASET_FILTERS.toSearchObject(id = "1234", timestamp = 1727765537686).dcatProfiles,
        )
    }

    @Test
    fun `datasets without dcatProfiles map to null`() {
        assertEquals(
            null,
            TEST_DATASET_HIT_ALL_FIELDS.toSearchObject(id = "1234", timestamp = 1727765537686).dcatProfiles,
        )
    }
}

val expectedAdditionalTitles =
    setOf(
        LocalizedStrings(
            "NB: Frarådet term",
            "NN: Fråråda term",
            "NO: Frarådet term",
            "EN: Hidden term",
        ),
        LocalizedStrings(
            "NB: Tillatt term",
            "NN: Tillatt term",
            "NO: Tillatt term",
            "EN: Alternative term",
        ),
    )
