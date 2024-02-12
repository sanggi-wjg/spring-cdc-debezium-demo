package com.example.springcdcdebeziumdemo.kafka

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties

@ConditionalOnProperty(prefix = "config.kafka", name = ["schema-registry-url"])
@ConfigurationProperties(prefix = "config.kafka")
data class KafkaProperty(
    val bootstrapServers: String,
    val schemaRegistryUrl: String,
    val specificAvroReader: Boolean,
    val consumer: ConsumerProperty,
    val producer: ProducerProperty,
) {
    data class ConsumerProperty(
        val autoOffsetReset: String,
        val keyDeserializer: String,
        val valueDeserializer: String,
        val concurrency: Int,
        val properties: Map<String, String> = mapOf(),
    )

    data class ProducerProperty(
        val keySerializer: String,
        val valueSerializer: String,
        val properties: Map<String, String> = mapOf(),
        val acks: String,
        val retries: Int,
        val compressionType: String,
    )
}