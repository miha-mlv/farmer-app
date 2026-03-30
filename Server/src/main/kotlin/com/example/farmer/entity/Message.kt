package com.example.farmer.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "messages")
data class Message(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val orderId: Long,
    val chatId: String, // orderId_farmerId_customerId
    val senderId: Long,
    val receiverId: Long,

    @Column(columnDefinition = "TEXT")
    val content: String? = null,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    var isRead: Boolean = false
)