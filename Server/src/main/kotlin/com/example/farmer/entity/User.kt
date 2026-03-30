package com.example.farmer.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val username: String,

    @Column(nullable = false)
    val role: String,

    @Column(nullable = false)
    val passwordHash: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var isEnabled: Boolean = false,
    var verificationCode: String? = null,
    val farmName: String? = null,
    val fcmToken: String?
)
