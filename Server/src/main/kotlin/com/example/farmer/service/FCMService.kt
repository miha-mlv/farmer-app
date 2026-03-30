package com.example.farmer.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service

@Service
class FCMService {
    fun sendNotification(token: String, title: String, message: String, orderId: Long) {
        val notification = Notification.builder()
            .setTitle(title)
            .setBody(message)
            .build()

        val msg = Message.builder()
            .setToken(token) // Токен получателя из БД
            .setNotification(notification)
            //.putData("orderId", orderId.toString()) Доп данные для перенаправления сразу к нужно заказу
            .build()

        FirebaseMessaging.getInstance().send(msg)
    }
}