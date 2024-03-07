package com.example.springcdcdebeziumdemo.consumer

import avro.schema.Event
import avro.schema.User
import com.example.springcdcdebeziumdemo.kafka.KafkaConfiguration
import com.example.springcdcdebeziumdemo.kafka.KafkaGroup
import com.example.springcdcdebeziumdemo.kafka.KafkaTopic
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ConsumerService {
    fun test(message: String)
    fun testUser(@Payload message: ConsumerRecord<String, Event>)
    fun changedDataCapture(@Payload message: ConsumerRecord<String, String>)
}

@Transactional
@Service
class BasicConsumerService(
    private val objectMapper: ObjectMapper,
) : ConsumerService {

    private val log = LoggerFactory.getLogger(BasicConsumerService::class.java)

    @KafkaListener(
        topics = [KafkaTopic.TEST],
        groupId = KafkaGroup.SPRING_TEST,
        containerFactory = KafkaConfiguration.LISTENER_CONTAINER_FACTORY,
    )
    override fun test(@Payload message: String) {
        log.info(message)
    }

    @KafkaListener(
        topics = [KafkaTopic.TEST_EVENT],
        groupId = KafkaGroup.SPRING_CDC,
        containerFactory = KafkaConfiguration.CDC_LISTENER_CONTAINER_FACTORY,
    )
    override fun testUser(@Payload message: ConsumerRecord<String, Event>) {
        log.info("testUser")
        log.info(message.key())
        log.info(message.value().toString())
    }

    @KafkaListener(
        topics = [KafkaTopic.DEMO_USER],
        groupId = KafkaGroup.SPRING_CDC,
        containerFactory = KafkaConfiguration.CDC_LISTENER_CONTAINER_FACTORY,
    )
    override fun changedDataCapture(@Payload message: ConsumerRecord<String, String>) {
        log.info("changedDataCapture")
        log.info(message.key())
        log.info(message.value().toString())
    }
}