package com.example.springcdcdebeziumdemo.kafka

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*

@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaProperty::class)
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
                KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG to property.schemaRegistryUrl,
                KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG to property.specificAvroReader,
            )
        )
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> {
        return DefaultKafkaProducerFactory(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to property.bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to property.producer.keySerializer,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to property.producer.valueSerializer,
                ProducerConfig.RETRIES_CONFIG to property.producer.retries,
                ProducerConfig.COMPRESSION_TYPE_CONFIG to property.producer.compressionType,
            ),
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, Any> = ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = consumerFactory()
        factory.setConcurrency(property.consumer.concurrency)
        return factory
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory())
    }
}