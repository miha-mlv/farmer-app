package com.example.farmer.common.network

data class RegisterRequest(
    val email: String,
    val username: String,
    val farmName: String?,
    val role: String,
    val password: String
)

data class FarmerProfile(
    val name: String,
    val farmName: String,
    val email: String
)

data class RegisterResponse(
    val message: String,
    val email: String,
    val requiresVerification: Boolean,
    val result: Boolean
)

data class VerificationRequest(
    val code: String,
    val email: String
)

data class VerificationResponse(
    val result: Boolean
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val result: Boolean,
    val role: String
)
