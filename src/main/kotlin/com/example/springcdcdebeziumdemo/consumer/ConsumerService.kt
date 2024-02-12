package com.example.springcdcdebeziumdemo.consumer

import com.example.springcdcdebeziumdemo.kafka.KafkaGroup
import com.example.springcdcdebeziumdemo.kafka.KafkaTopic
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ConsumerService {
    fun changedDataCapture(message: String)
}

@Transactional
@Service
class BasicConsumerService(
    private val objectMapper: ObjectMapper,
) : ConsumerService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [KafkaTopic.DEMO_USER],
        groupId = KafkaGroup.SPRING_DEMO
    )
    override fun changedDataCapture(@Payload message: String) {
        log.info(message)
    }
}