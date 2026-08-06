package no.digdir.fdk.searchservice.kafka

import org.apache.avro.generic.GenericRecord

fun GenericRecord.safeGetString(key: String): String? =
    try {
        get(key)?.toString()?.takeIf { it.isNotBlank() }
    } catch (e: Exception) {
        null
    }

fun GenericRecord.getHarvestRunId(): String? = safeGetString("harvestRunId")

fun GenericRecord.getUri(): String? = safeGetString("uri")

fun GenericRecord.getTimestamp(): Long = (get("timestamp") as? Number)?.toLong() ?: 0L
