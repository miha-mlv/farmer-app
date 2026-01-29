package com.example.farmer.controller

import com.example.farmer.dto.*
import com.example.farmer.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): RegisterResponse {
        return try {
            authService.register(request)
        } catch (e: Exception) {
            RegisterResponse(e.message.toString(), request.email, false, false)
        }
    }

    @PostMapping("/verif")
    fun verification(@RequestBody request: VerificationRequest): VerificationResponse{
        return try{
            authService.verification(request)
        }catch (e: Exception){
            VerificationResponse(false)
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse{
        return try{
            authService.login(request)
        }catch (e: Exception){
            LoginResponse("0", false, e.message.toString())
        }
    }

//    @PostMapping("/")



}