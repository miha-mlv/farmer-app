package com.example.farmer.customer.data.network.model

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