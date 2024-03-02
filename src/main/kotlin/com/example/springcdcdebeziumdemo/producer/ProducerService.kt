package com.example.springcdcdebeziumdemo.producer

import com.example.springcdcdebeziumdemo.kafka.KafkaConfiguration
import com.example.springcdcdebeziumdemo.kafka.KafkaTopic
import org.apache.avro.generic.GenericRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import schema.data.User

interface ProducerService {
    fun publishTest()
    fun publishUser()
}

@Service
class BasicProducerService(
    private val kafkaProducer: KafkaTemplate<String, String>,
    @Qualifier(KafkaConfiguration.CDC_KAFKA_TEMPLATE)
    private val cdcKafkaProducer: KafkaTemplate<String, GenericRecord>,
) : ProducerService {

    private val log = LoggerFactory.getLogger(this::class.java)

    //    @TransactionalEventListener
    override fun publishTest() {
        log.info("Publishing test message")
        kafkaProducer.send(KafkaTopic.TEST, "test")
    }

    override fun publishUser() {
        log.info("Publishing user message")
        cdcKafkaProducer.send(KafkaTopic.TEST_USER, User(1, "test", "test", "test", "ACTIVE"))
    }
}