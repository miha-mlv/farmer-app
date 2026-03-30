package com.example.farmer.repository

import com.example.farmer.entity.Message
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface MessageRepository : JpaRepository<Message, Long> {
    // Получить всю историю переписки по заказу
    fun findByOrderId(orderId: Long): List<Message>

    // Найти все непрочитанные сообщения, где текущий юзер — получатель
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.orderId = :orderId AND m.receiverId = :userId AND m.isRead = false")
    fun markMessagesAsRead(orderId: Long, userId: Long)

    fun countByOrderIdAndReceiverIdAndIsReadFalse(orderId: Long, receiverId: Long): Int
}