package com.example.springcdcdebeziumdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.example.springcdcdebeziumdemo")
@SpringBootApplication(
    scanBasePackages = ["com.example.springcdcdebeziumdemo"],
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
