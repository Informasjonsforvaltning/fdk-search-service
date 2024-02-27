package no.digdir.fdk.searchservice.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.elastic")
data class ElasticProperties(
    val username: String,
    val password: String,
    val host: String,
    val ssl: Boolean,
    val storePath: String,
    val storePass: String
)
