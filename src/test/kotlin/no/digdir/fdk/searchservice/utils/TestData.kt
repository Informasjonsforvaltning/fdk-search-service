package no.digdir.fdk.searchservice.utils

import no.digdir.fdk.searchservice.model.*
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap

val ELASTIC_ENV_VALUES: Map<String, String> = ImmutableMap.of(
        "cluster.name", "elasticsearch",
        "discovery.type", "single-node",
        "xpack.security.enabled", "true",
        "ELASTIC_PASSWORD","elasticpwd",
        "ES_JAVA_OPTS", "-Xms2G -Xmx2G"
)

val TEST_DATASET_HIT_1 = Dataset(
        id = "123",
        title = LocalizedStrings("NB Test title","NN Test title","EN Test title"),
        description = LocalizedStrings("NB Test description","NN Test description","EN Test description"),
)

val TEST_DATASET_HIT_ALL_FIELDS = Dataset(
        id = "123",
        title = LocalizedStrings("NB Test title","NN Test title","EN Test title"),
        description = LocalizedStrings("NB Test description",
                "NN Test description",
                "EN Test description"),
        objective = LocalizedStrings("NB Test objective",
                "NN Test objective",
                "EN Test objective"),
        keyword = listOf(LocalizedStrings("NB Test keyword",
                "NN Test keyword",
                "EN Test keyword")),
        theme = listOf(EuDataTheme(LocalizedStrings("NB Test theme",
                "NN Test theme",
                "EN Test theme"))),
        losTheme = listOf(LosNode(LocalizedStrings("NB Test losTheme",
                "NN Test losTheme",
                "EN Test losTheme"))),
        publisher = Publisher("Test publisher",
                LocalizedStrings("NB Test publisher",
                        "NN Test publisher",
                        "EN Test publisher")),
        accessRights = ReferenceDataCode("Test accessRights",
                LocalizedStrings("NB Test accessRights",
                        "NN Test accessRights",
                        "EN Test accessRights")),
        subject = listOf(Subject(LocalizedStrings("NB Test subject",
                "NN Test subject",
                "EN Test subject"),
                LocalizedStrings("NB Test subject",
                        "NN Test subject",
                        "EN Test subject"),
                LocalizedStrings("NB Test subject",
                        "NN Test subject",
                        "EN Test subject"))),
        distribution = listOf(Distribution(LocalizedStrings("NB Test distribution",
                "NN Test distribution",
                "EN Test distribution")))
)
