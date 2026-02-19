package no.digdir.fdk.searchservice.kafka

import org.apache.avro.generic.GenericRecord

fun GenericRecord.safeGetString(key: String): String? = try {
    get(key)?.toString()?.takeIf { it.isNotBlank() }
} catch (e: Exception) {
    null
}
