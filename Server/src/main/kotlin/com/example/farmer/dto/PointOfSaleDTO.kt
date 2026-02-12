package com.example.farmer.dto

data class PointOfSaleRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String
)

data class PosStatusRequest(
    val isActive: Boolean,
    val productsIds: List<Long>? = null // Список товаров передаем при активации
)

data class ProductWithPosDto(
    // Данные товара
    val productId: Long,
    val productName: String,
    val price: Double,
    val farmName: String, // Название фермы или точки
    val description: String?,
    val image: String?,

    // Данные точки продажи
    val posName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)