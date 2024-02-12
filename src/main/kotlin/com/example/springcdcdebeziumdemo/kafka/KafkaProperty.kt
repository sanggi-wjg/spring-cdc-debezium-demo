package com.example.springcdcdebeziumdemo.kafka

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties

@ConditionalOnProperty(prefix = "config.kafka", name = ["bootstrap-server"], havingValue = "kafka")
@ConfigurationProperties(prefix = "config.kafka")
data class KafkaProperty(
    val bootstrapServers: String,
    val schemaRegistryUrl: String,
    val specificAvroReader: Boolean,
    val consumer: ConsumerProperty,
) {
    data class ConsumerProperty(
        val autoOffsetReset: String,
        val keyDeserializer: String,
        val valueDeserializer: String,
        val properties: Map<String, String> = mapOf(),
    )
}