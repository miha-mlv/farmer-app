package com.example.farmer.dto

data class PointOfSaleRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String
)

data class PosStatusRequest(
    val isActive: Boolean,
    val productIds: List<Long>? = null // Список товаров передаем при активации
)