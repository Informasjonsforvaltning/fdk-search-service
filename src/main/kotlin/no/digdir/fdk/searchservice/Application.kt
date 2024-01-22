package no.digdir.fdk.searchservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@SpringBootApplication
@ConfigurationPropertiesScan
open class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
