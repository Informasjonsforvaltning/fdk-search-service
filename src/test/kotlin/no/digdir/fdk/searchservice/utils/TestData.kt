package no.digdir.fdk.searchservice.utils

import no.digdir.fdk.searchservice.model.TempTestDBO
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap

val ELASTIC_ENV_VALUES: Map<String, String> = ImmutableMap.of(
        "cluster.name", "elasticsearch",
        "discovery.type", "single-node",
        "xpack.security.enabled", "true",
        "ELASTIC_PASSWORD","elasticpwd",
        "ES_JAVA_OPTS", "-Xms2G -Xmx2G"
)

val TEMP_TEST_OBJECT = TempTestDBO(
        id = "123",
        test = "Hei"
)
