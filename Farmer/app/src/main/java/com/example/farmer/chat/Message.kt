package com.example.farmer.chat

data class Message(
    val id: Long? = null,
    val orderId: Long,
    val chatId: String,
    val senderId: Long,
    val receiverId: Long,
    val content: String? = null,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    var isRead: Boolean = false
)