package com.example.farmer.controller

import com.example.farmer.entity.Message
import com.example.farmer.entity.OrderStatus
import com.example.farmer.repository.MessageRepository
import com.example.farmer.repository.OrderRepository
import jakarta.transaction.Transactional
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController(
    private val messageRepository: MessageRepository,
    private val orderRepository: OrderRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {

    // --- HTTP Методы (для Retrofit) ---

    @GetMapping("/api/chat/{orderId}/history")
    fun getChatHistory(@PathVariable orderId: Long): List<Message> {
        // Здесь должен быть метод твоего репозитория, который ищет сообщения по ID заказа
        // Например: return messageRepository.findAllByOrderIdOrderByTimestampAsc(orderId)
        return messageRepository.findByOrderId(orderId)
    }

    // --- WebSocket Методы (для Stomp) ---
    @MessageMapping("/chat.send")
    fun sendMessage(@Payload message: Message) {
        println("Получено сообщение для заказа: ${message.orderId}") // ЛОГ ДЛЯ ПРОВЕРКИ

        val order = orderRepository.findById(message.orderId).orElse(null)
        if (order == null) {
            println("Ошибка: Заказ ${message.orderId} не найден в базе!")
            return
        }
        val statusesToBlock = listOf(OrderStatus.COMPLETED, OrderStatus.REJECTED, OrderStatus.CANCELLED)
        if (order.status in statusesToBlock) return

        // Проверка статуса...
        val savedMessage = messageRepository.save(message)
        messagingTemplate.convertAndSend(
            "/queue/messages/" + message.senderId,
            savedMessage
        )
        // 1. Отправляем ПОЛУЧАТЕЛЮ (чтобы у него обновилось)
        messagingTemplate.convertAndSend(
            "/queue/messages/" + message.receiverId,
            savedMessage
        )

        println("Сообщение сохранено с ID: ${savedMessage.id}")
    }

    @MessageMapping("/chat.readAll")
    @Transactional
    fun readAll(@Payload data: Map<String, Long>) {
        val orderId = data["orderId"] ?: return
        val userId = data["userId"] ?: return // Тот, КТО прочитал
        val receiverId = data["receiverId"] ?: return // Тот, КОМУ мы сообщаем о прочтении

        messageRepository.markMessagesAsRead(orderId, userId)

        // 2. Формируем сигнал для фронтенда
        val statusUpdate = mapOf(
            "status" to "READ_ALL",
            "orderId" to orderId,
            "readerId" to userId
        )

        // 3. Отправляем ВТОРОМУ участнику чата, что его сообщения прочитаны
        messagingTemplate.convertAndSend(
            "/queue/messages/$receiverId",
            statusUpdate as Any)
    }
}