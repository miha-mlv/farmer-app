package com.example.farmer.chat

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class ChatRepository(
    private val apiService: ChatApi,
    private val stompManager: ChatStompManager
) {
    // Получаем историю через REST
    suspend fun getChatHistory(orderId: Long): List<Message> {
        return try {
            val history = apiService.getChatHistory(orderId)
            Log.d("ReturnChatRepo", "History chat: $history")
            history
        } catch (e: Exception) {
            Log.e("ChatRepo", "Ошибка при загрузке истории: ${e.message}")
            emptyList()
        }
    }

    // Загрузка фото на сервер
    suspend fun uploadImage(file: File): Response<Map<String, String>> {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return apiService.uploadImage(body)
    }

    // Подключение к WebSocket
    fun connectToWebSocket(
        wsUrl: String,
        onMessageReceived: (Message) -> Unit,
        onConnected: () -> Unit = {},
        onError: (String) -> Unit,
        onStatusReceived: (Long) -> Unit
    ) {
        stompManager?.connect(wsUrl, onMessageReceived, onConnected, onError, onStatusReceived)
    }

    // Отправка через WebSocket
    fun sendMessage(message: Message) {
        stompManager.sendMessage(message)
    }

    // Отправка статуса прочтения
    fun sendReadStatus(orderId: Long, userId: Long, receiverId: Long) {
        stompManager.sendReadStatus(orderId, userId, receiverId)
    }

    fun disconnect() {
        stompManager.disconnect()
    }
}
