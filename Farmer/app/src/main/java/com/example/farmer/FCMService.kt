package com.example.farmer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.farmer.common.auth.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    // Вызывается, когда генерируется новый токен (при первом запуске)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // ОТПРАВЬ этот токен на свой сервер и привяжи к текущему пользователю в БД
        Log.d("FCM", "Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(message.notification?.title, message.notification?.body)
    }

    private fun showNotification(title: String?, message: String?) {

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "orders_channel"

        // 1. Создаем канал (только для Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Заказы", // Имя канала, которое увидит пользователь в настройках
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления об изменении статуса ваших заказов"
                enableLights(true)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 2. Строим уведомление
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Проверь, что иконка есть!
            .setContentTitle(title ?: "Новое уведомление")
            .setContentText(message ?: "Проверьте статус заказа")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // 3. Показываем
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}