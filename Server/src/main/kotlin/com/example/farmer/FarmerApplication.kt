package com.example.farmer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FarmerApplication

fun main(args: Array<String>) {
    try {
        runApplication<FarmerApplication>(*args)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
