package no.digdir.fdk.searchservice.utils
import no.digdir.fdk.searchservice.elastic.SearchObjectRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.elasticsearch.ElasticsearchContainer
import java.net.HttpURLConnection
import java.net.URL

abstract class ApiTestContext {

    @LocalServerPort
    var port = 0

    @Autowired
    private lateinit var repository: SearchObjectRepository

    @BeforeEach
    fun populateElastic() {
        repository.deleteAll()
        repository.save(TEMP_TEST_OBJECT)
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                    "application.elastic.host=localhost:${elasticContainer.getMappedPort(9200)}"
            ).applyTo(configurableApplicationContext.environment)
        }
    }

    companion object {
        val elasticContainer: ElasticsearchContainer = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.10.2")
            .withEnv(ELASTIC_ENV_VALUES)
            .waitingFor(LogMessageWaitStrategy().withRegEx(".*\"message\":\"started.*"))

        init {
            elasticContainer.start()
            try {
                val con = URL("http://localhost:5050/ping").openConnection() as HttpURLConnection
                con.connect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}