package com.example.farmer.farmer.network

import com.example.farmer.farmer.AddNewProductFragment
import java.time.LocalDateTime

data class ProductRequest(
    val name: String,
    val category: String, // Передаем name() из Enum (например, "VEGETABLES")
    val price: Double,
    val quantity: Int,
    val description: String,
    val farmerToken: String
)

data class Product(
    val id: Long,

    var name: String,

    var category: AddNewProductFragment.ProductCategory,

    var price: Double,

    var quantity: Int,

    var description: String? = null,

    var isActive: Boolean = true,

    var images: MutableList<String> = mutableListOf(),

    var farmer: User
)

data class TokenRequest(
    val token: String
)

data class User(
    val id: Long? = null,

    val email: String,

    val username: String,

    val role: String,

    val passwordHash: String,

    val createdAt: String,

    var isEnabled: Boolean = false,

    var verificationCode: String? = null,

    val farmName: String? = null
)

data class PointOfSaleRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String
)

data class PointOfSale(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String
)

data class PosStatusRequest(
    val isActive: Boolean,
    val productsIds: List<Long>? = null
)