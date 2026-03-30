package no.digdir.fdk.searchservice.elastic

import no.digdir.fdk.searchservice.configuration.ElasticProperties
import co.elastic.clients.transport.TransportOptions
import co.elastic.clients.transport.rest5_client.Rest5ClientOptions
import co.elastic.clients.transport.rest5_client.low_level.RequestOptions
import org.apache.hc.core5.ssl.SSLContextBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import java.time.Duration
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

@Configuration
@EnableElasticsearchRepositories
class ElasticsearchConfig(private val elasticProperties: ElasticProperties) : ElasticsearchConfiguration() {

    private fun sslContext(): SSLContext {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        FileInputStream(File(elasticProperties.storePath)).use { input ->
            keyStore.load(input, elasticProperties.storePass.toCharArray())
        }
        
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        
        return SSLContextBuilder.create()
            .loadTrustMaterial(keyStore, null)
            .build()
    }

    @Bean(name = ["elasticsearchClientConfiguration"])
    override fun clientConfiguration(): ClientConfiguration {
        val builder = ClientConfiguration.builder()
            .connectedTo(elasticProperties.host)

        if (elasticProperties.ssl) builder.usingSsl(sslContext())

        builder.withBasicAuth(elasticProperties.username, elasticProperties.password)
            .withConnectTimeout(Duration.ofSeconds(120))

        return builder.build()
    }

    override fun transportOptions(): TransportOptions {
        val acceptRequestOptions = RequestOptions.DEFAULT
            .toBuilder()
            .addHeader("Accept", "application/vnd.elasticsearch+json;compatible-with=8")
            .addHeader("Content-Type", "application/vnd.elasticsearch+json;compatible-with=8")
            .build()

        return Rest5ClientOptions(acceptRequestOptions, false)
    }
}
