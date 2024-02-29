package com.example.springcdcdebeziumdemo.consumer

import com.example.springcdcdebeziumdemo.kafka.KafkaTopic
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

interface ProducerService {
    fun publishTestMessage()
}

@Service
class BasicProducerService(
    private val kafkaProducer: KafkaTemplate<String, String>
) : ProducerService {

    private val log = LoggerFactory.getLogger(this::class.java)

//    @TransactionalEventListener
    override fun publishTestMessage() {
        log.info("Publishing test message")
        kafkaProducer.send(KafkaTopic.TEST, "test")
    }
}