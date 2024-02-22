package com.example.springcdcdebeziumdemo.consumer

import com.example.springcdcdebeziumdemo.kafka.KafkaTopic
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

interface ProducerService {
    fun publishTestMessage()
}

@Service
class BasicProducerService(
    private val kafkaProducer: KafkaTemplate<String, Any>
) : ProducerService {

//    @TransactionalEventListener
    override fun publishTestMessage() {
        kafkaProducer.send(KafkaTopic.TEST, "test")
    }
}