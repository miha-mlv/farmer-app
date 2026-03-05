package com.example.farmer.dto

import com.example.farmer.entity.OrderStatus

data class OrderRequest(
    val totalAmount: Int,
    val products: List<BasketItem>,
    val createdAt: Long,
    val token: String
)

data class BasketItem(
    val id: Long,
    val name: String,
    val priceText: String,
    val farmName: String,
    val imageUrl: String?,
    val quantity: Int = 1,
    val farmerId: Long
)

data class OrderResponse(
    val id: Long,
    val farmerId: Long,
    val farmerName: String, // Добавим имя фермера для заголовка
    val customerId: Long,
    val totalAmount: Int,
    val status: OrderStatus,
    val createdAt: Long,
    val products: List<OrderItemDto>,

    // Поля для отказа
    val rejectionReason: String? = null,
    val rejectionComment: String? = null,
    val rejectedAt: Long? = null
)

data class OrderItemDto(
    val productId: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String? = null // Пригодится для иконок в списке
)

data class OrderUpdateRequest(
    val token: String,
    val id: Long,
    val farmerId: Long,
    val farmerName: String,
    val customerId: Long,
    val totalAmount: Int,
    val status: OrderStatus,
    val createdAt: Long,
    val products: List<OrderItemDto>,

    // Поля для отказа
    val rejectionReason: String? = null,
    val rejectionComment: String? = null,
    val rejectedAt: Long? = null
)