package com.example.farmer

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service

@Service
class NotificationService {

    fun sendNotification(targetToken: String, title: String, body: String) {
        if (targetToken.isEmpty()) return

        val message = Message.builder()
            .setToken(targetToken)
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build()
            )
            .build()

        try {
            FirebaseMessaging.getInstance().send(message)
            println("Successfully sent message to token: $targetToken")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}