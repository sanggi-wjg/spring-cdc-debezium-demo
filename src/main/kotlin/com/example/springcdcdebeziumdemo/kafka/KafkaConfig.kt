package com.example.springcdcdebeziumdemo.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory

@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaProperty::class)
@ConditionalOnProperty(prefix = "config.kafka", name = ["bootstrap-server"], havingValue = "kafka")
class KafkaConfig(
    private val property: KafkaProperty,
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        return DefaultKafkaConsumerFactory(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to property.bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to property.consumer.keyDeserializer,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to property.consumer.valueDeserializer,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to property.consumer.autoOffsetReset,
                ConsumerConfig.GROUP_ID_CONFIG to KafkaGroup.SPRING_DEMO,
                // Schema Registry, Avro 관련 설정 필수
//                KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG to property.schemaRegistryUrl,
//                KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG to property.specificAvroReader,
            )
        )
    }
}