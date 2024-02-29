package com.example.springcdcdebeziumdemo.kafka

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*

@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaProperty::class)
@ConditionalOnClass(KafkaTemplate::class)
class KafkaConfiguration(
    private val property: KafkaProperty,
) {
    companion object {
        const val CDC_LISTENER_CONTAINER_FACTORY = "cdcListenerContainerFactory"
        const val LISTENER_CONTAINER_FACTORY = "listenerContainerFactory"
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory(
            property.consumer.properties + mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to property.bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to property.consumer.keyDeserializer,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to "org.apache.kafka.common.serialization.StringDeserializer",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to property.consumer.autoOffsetReset,
                ConsumerConfig.GROUP_ID_CONFIG to KafkaGroup.SPRING_TEST,
            ),
        )
    }

    @Bean
    fun cdcConsumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory(
            property.consumer.properties + mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to property.bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to property.consumer.keyDeserializer,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to property.consumer.valueDeserializer,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to property.consumer.autoOffsetReset,
                ConsumerConfig.GROUP_ID_CONFIG to KafkaGroup.SPRING_CDC,
                KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG to property.schemaRegistryUrl,
                KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG to property.specificAvroReader,
            ),
        )
    }


    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(
            property.producer.properties + mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to property.bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to property.producer.keySerializer,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to property.producer.valueSerializer,
                ProducerConfig.ACKS_CONFIG to property.producer.acks,
                ProducerConfig.RETRIES_CONFIG to property.producer.retries,
                ProducerConfig.COMPRESSION_TYPE_CONFIG to property.producer.compressionType,
            ),
        )
    }

    @Bean(LISTENER_CONTAINER_FACTORY)
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, String> = ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = consumerFactory()
        factory.setConcurrency(property.consumer.concurrency)
        return factory
    }

    @Bean(CDC_LISTENER_CONTAINER_FACTORY)
    fun kafkaCdcListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, String> = ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = cdcConsumerFactory()
        factory.setConcurrency(property.consumer.concurrency)
        return factory
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}