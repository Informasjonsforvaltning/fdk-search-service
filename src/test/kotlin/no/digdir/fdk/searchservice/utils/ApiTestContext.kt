package no.digdir.fdk.searchservice.utils

import no.digdir.fdk.searchservice.data.*
import no.digdir.fdk.searchservice.elastic.SearchRepository
import no.digdir.fdk.searchservice.mapper.toSearchObject
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.elasticsearch.ElasticsearchContainer
import java.net.HttpURLConnection
import java.net.URI

abstract class ApiTestContext {

    @LocalServerPort
    var port = 0

    @Autowired
    private lateinit var repository: SearchRepository

    @BeforeEach
    fun populateElastic() {
        repository.deleteAll()
        repository.saveAll(
            listOf(
                TEST_SEARCH_OBJECT_AND_HIT_ALL_FIELDS,
                TEST_DATASET_HIT_ALL_FIELDS.toSearchObject("1", System.currentTimeMillis()),
                TEST_DATASET_HIT_IS_OPEN.toSearchObject("2", System.currentTimeMillis()),
                TEST_CONCEPT_HIT_ALL_FIELDS.toSearchObject("1021", System.currentTimeMillis()),
                TEST_DATA_SERVICE_HIT_ALL_FIELDS.toSearchObject("456", System.currentTimeMillis()),
                TEST_EVENT_HIT_ALL_FIELDS.toSearchObject("1061", System.currentTimeMillis()),
                TEST_INFORMATION_MODEL_HIT_ALL_FIELDS.toSearchObject("1081", System.currentTimeMillis()),
                TEST_SERVICE_HIT_ALL_FIELDS.toSearchObject("0101", System.currentTimeMillis()),
                TEST_SERVICE_HIT_OWNED_BY.toSearchObject("0102", System.currentTimeMillis()),
                TEST_SERVICE_HIT_HAS_COMPETENT_AUTHORITY.toSearchObject("0103", System.currentTimeMillis()),
                TEST_DATASET_FILTERS.toSearchObject("3", System.currentTimeMillis()),
                TEST_SERVICE.toSearchObject("678", System.currentTimeMillis()),
                TEST_EVENT.toSearchObject("111", System.currentTimeMillis())
            ))
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "application.elastic.host=localhost:${elasticContainer.getMappedPort(9200)}",
                "application.cors.originPatterns=*",
            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {
        val elasticContainer: ElasticsearchContainer =
            ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.10.2")
                .withEnv(ELASTIC_ENV_VALUES)
                .waitingFor(LogMessageWaitStrategy().withRegEx(".*\"message\":\"started.*"))

        init {
            elasticContainer.start()
            try {
                val con = URI.create("http://localhost:5050/ping").toURL().openConnection() as HttpURLConnection
                con.connect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
