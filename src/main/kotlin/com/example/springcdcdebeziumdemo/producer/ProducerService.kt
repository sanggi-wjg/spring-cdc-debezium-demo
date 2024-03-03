package com.example.springcdcdebeziumdemo.producer

import avro.schema.Event
import com.example.springcdcdebeziumdemo.kafka.KafkaConfiguration
import com.example.springcdcdebeziumdemo.kafka.KafkaTopic
import org.apache.avro.generic.GenericRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import kotlin.random.Random

interface ProducerService {
    fun publishTest()
    fun publishTestEvent()
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

    override fun publishTestEvent() {
        log.info("Publishing user message")
        cdcKafkaProducer.send(
            KafkaTopic.TEST_EVENT,
            Event(Random.nextInt(100), "Random event created", Timestamp.from(Instant.now()).time.toInt()),
        )
    }
}