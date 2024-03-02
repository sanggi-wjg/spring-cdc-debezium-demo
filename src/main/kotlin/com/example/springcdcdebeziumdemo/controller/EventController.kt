package com.example.springcdcdebeziumdemo.controller

import com.example.springcdcdebeziumdemo.producer.ProducerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class EventController(
    private val producerService: ProducerService
) {

    @GetMapping("/test")
    fun publishTest(): ResponseEntity<String> {
        producerService.publishTest()
        return ResponseEntity.ok("Success")
    }

    @GetMapping("/test-user")
    fun publishTestUser(): ResponseEntity<String> {
        producerService.publishUser()
        return ResponseEntity.ok("Success")
    }
}