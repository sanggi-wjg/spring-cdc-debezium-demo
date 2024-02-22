package com.example.springcdcdebeziumdemo.controller

import com.example.springcdcdebeziumdemo.consumer.ProducerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(
    private val producerService: ProducerService
) {

    @GetMapping("/")
    fun index(): ResponseEntity<String> {
        producerService.publishTestMessage()
        return ResponseEntity.ok("Success")
    }
}