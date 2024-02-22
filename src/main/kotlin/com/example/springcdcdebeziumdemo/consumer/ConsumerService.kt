package com.example.springcdcdebeziumdemo.consumer

import com.example.springcdcdebeziumdemo.kafka.KafkaConfiguration
import com.example.springcdcdebeziumdemo.kafka.KafkaGroup
import com.example.springcdcdebeziumdemo.kafka.KafkaTopic
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface ConsumerService {
    fun test(message: String)
    fun changedDataCapture(message: String)
}

@Transactional
@Service
class BasicConsumerService(
//    private val objectMapper: ObjectMapper,
) : ConsumerService {

    private val log = LoggerFactory.getLogger(BasicConsumerService::class.java)

    @KafkaListener(
        topics = [KafkaTopic.TEST],
        groupId = KafkaGroup.SPRING_DEMO,
        containerFactory = KafkaConfiguration.LISTENER_CONTAINER_FACTORY,
    )
    override fun test(@Payload message: String) {
        log.info(message)
    }

    @KafkaListener(
        topics = [KafkaTopic.DEMO_USER],
        groupId = KafkaGroup.SPRING_DEMO,
        containerFactory = KafkaConfiguration.CDC_LISTENER_CONTAINER_FACTORY,
    )
    override fun changedDataCapture(@Payload message: String) {
        log.info(message)
    }
}