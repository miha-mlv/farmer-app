package com.example.farmer.service

import com.example.farmer.dto.LoginRequest
import com.example.farmer.dto.LoginResponse
import com.example.farmer.dto.RegisterRequest
import com.example.farmer.dto.RegisterResponse
import com.example.farmer.dto.VerificationRequest
import com.example.farmer.dto.VerificationResponse
import com.example.farmer.entity.User
import com.example.farmer.repository.UserRepository
import org.apache.logging.log4j.message.SimpleMessage
import org.springframework.mail.SimpleMailMessage
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Service слой для логики аутентификации
 * Содержит бизнес-логику регистрации и авторизации
 *
 * @Service - помечает класс как сервис Spring
 */
@Service
class AuthService(
    // Внедрение зависимостей через конструктор (Dependency Injection)
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
    private val jwtService: JwtService
) {
    fun register(request: RegisterRequest): RegisterResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw RuntimeException("Пользователь с таким email уже существует")
        }

        val verificationCode = (100000..999999).random().toString()

        val user = User(
            email = request.email,
            username = request.username,
            passwordHash = passwordEncoder.encode(request.password).toString(),
            role = request.role,
            farmName = request.farmName,
            verificationCode = verificationCode
        )

        emailService.sendVerificationCode(request.email, verificationCode)
        userRepository.save(user)


        return RegisterResponse(
            message = "Успешная регистрация",
            email = request.email,
            requiresVerification = false,
            true
        )
    }

    fun verification(request: VerificationRequest): VerificationResponse {
        if (!userRepository.existsByEmail(request.email)) {
            throw Exception("Пользователь не найден")
        }
        val user = userRepository.findByEmail(request.email)
        if (request.code == user.get().verificationCode) {
            user.get().isEnabled = true
            userRepository.save(user.get())
            return VerificationResponse(true)
        }
        return VerificationResponse(false)
    }

    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { RuntimeException("Неверный email или пароль") }

        if (user.email != request.email) {
            throw RuntimeException("Неверный email или пароль")
        }

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw RuntimeException("Неверный email или пароль")
        }

        val token = jwtService.generateToken(user)

        return LoginResponse(
            token = token,
            result = true,
            role = user.role
        )
    }
}