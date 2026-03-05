package com.example.farmer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.data.web.config.EnableSpringDataWebSupport

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
class FarmerApplication

fun main(args: Array<String>) {
    try {
        runApplication<FarmerApplication>(*args)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
