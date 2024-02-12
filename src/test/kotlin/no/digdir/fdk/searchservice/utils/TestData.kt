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

/**
 * DATASET
 */

val TEST_NULL_DATASET = Dataset("123", null, null, null, null, null, null, null, null, null, null, null)

val TEST_DATASET_HIT_1 = TEST_NULL_DATASET.copy(
        id = "123",
        title = LocalizedStrings("NB Test title", "NN Test title", "EN Test title"),
        description = LocalizedStrings("NB Test description", "NN Test description", "EN Test description"),
        isOpenData = false,
        theme = listOf(EuDataTheme(
                LocalizedStrings("NB theme", "NN theme", "EN theme"),
                "REGI")
        )
)

val TEST_DATASET_HIT_ALL_FIELDS = TEST_NULL_DATASET.copy(
        id = "124",
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
                "EN Test theme"), "ENVI"),
                EuDataTheme(LocalizedStrings("NB Test theme",
                        "NN Test theme",
                        "EN Test theme"), "REGI")),
        losTheme = listOf(LosNode(LocalizedStrings("NB Test losTheme",
                "NN Test losTheme",
                "EN Test losTheme"))),
        publisher = Publisher("Test publisher",
                LocalizedStrings("NB Test publisher",
                        "NN Test publisher",
                        "EN Test publisher")),
        accessRights = ReferenceDataCode("PUBLIC",
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
                        "EN Test subject"),
                LocalizedStrings("NB Test subject",
                        "NN Test subject",
                        "EN Test subject"))),
        distribution = listOf(Distribution(LocalizedStrings("NB Test distribution",
                "NN Test distribution",
                "EN Test distribution"))),
        isOpenData = true
)

/**
 * CONCEPT
 */

val TEST_NULL_CONCEPT = Concept("123", null, null, null, null)

val TEST_CONCEPT_HIT_SUCCESS_1 = TEST_NULL_CONCEPT.copy(
        id = "123",
        publisher = Publisher(
                "Test publisher > name",
                LocalizedStrings(
                        "NB Test publisher > prefLabel",
                        "NN Test publisher > prefLabel",
                        "EN Test publisher > prefLabel")),
        definition = Definition(
                LocalizedStrings(
                        "NB Test definition > text",
                        "NN Test definition > text",
                        "EN Test definition > text"),
                Sources(listOf(TextAndURI(LocalizedStrings(
                        "NB Test definition > sources > text",
                        "NN Test definition > sources > text",
                        "EN Test definition > sources > text")))),
                "Test definition > sourceRelationship"),
        prefLabel = LocalizedStrings(
                        "NB Test prefLabel",
                        "NN Test prefLabel",
                        "EN Test prefLabel"),
        subject = listOf(Subject(
                LocalizedStrings(
                        "NB Test subject > altLabel",
                        "NN Test subject > altLabel",
                        "EN Test subject > altLabel"),
                LocalizedStrings(
                        "NB Test subject > definition",
                        "NN Test subject > definition",
                        "EN Test subject > definition"),
                LocalizedStrings(
                        "NB Test subject > prefLabel",
                        "NN Test subject > prefLabel",
                        "EN Test subject > prefLabel"),
                LocalizedStrings(
                        "NB Test subject > label",
                        "NN Test subject > label",
                        "EN Test subject > label")))
)

/**
 * DATASERVICE
 */

val TEST_NULL_DATASERVICE = Dataservice("123", null, null, null)

val TEST_DATASERVICE_HIT_SUCCESS_1 = TEST_NULL_DATASERVICE.copy(
        id = "123",
        title = LocalizedStrings(
                "NB Test title",
                "NN Test title",
                "EN Test title"),
        description = LocalizedStrings(
                "NB Test description",
                "NN Test description",
                "EN Test description"),
        publisher = Publisher(
                "Test publisher > name",
                LocalizedStrings(
                        "NB Test publisher > prefLabel",
                        "NN Test publisher > prefLabel",
                        "EN Test publisher > prefLabel"))
)
