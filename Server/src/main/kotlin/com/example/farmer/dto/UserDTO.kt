package com.example.farmer.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Email не может быть пустым")
    @field:Email(message = "Некорректный формат email")
    val email: String,

    @field:NotBlank(message = "Имя пользователя не может быть пустым")
    @field:Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    val username: String,

    val farmName: String? = null, // Передается только если role == "FARMER"

    val role: String,

    @field:NotBlank(message = "Пароль не может быть пустым")
    @field:Size(min = 6, max = 100, message = "Пароль должен быть от 6 до 100 символов")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$",
        message = "Пароль должен содержать цифру, строчную и заглавную буквы"
    )
    val password: String
)

data class RegisterResponse(
    val message: String,
    val email: String,
    val requiresVerification: Boolean = false,
    val result: Boolean
)

data class VerificationRequest(
    @field: NotBlank(message="Неверный код")
    val code: String,
    @field: NotBlank(message="Нету почты")
    val email: String
)

data class VerificationResponse(
    val result: Boolean
)


data class LoginRequest(
    @field:NotBlank(message = "Email не может быть пустым")
    @field:Email(message = "Некорректный формат email")
    val email: String,

    @field:NotBlank(message = "Пароль не может быть пустым")
    val password: String,
    val fcmToken: String?
)

data class LoginResponse(
    val token: String = "none",
    val result: Boolean,
    val role: String,
    val userId: Long
)