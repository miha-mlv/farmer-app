package com.example.farmer.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.farmer.common.util.TokenManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel(), ViewModelProvider.Factory {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()
    private val _error = MutableSharedFlow<String>(replay = 0)
    val error = _error.asSharedFlow()

    private var currentOrderId: Long = 0L
    private var currentSenderId: Long = 0L
    private var currentReceiverId: Long = 0L


    fun initChat(orderId: Long) {
        viewModelScope.launch {
            try {
                // 1. Загружаем историю из репозитория
                val history = repository.getChatHistory(orderId)
                _messages.value = history
                Log.d("init chat history", "$history")

                // 2. Подключаем сокеты через репозиторий
                val wsUrl = "ws://10.0.2.2:8080/ws-chat"
                repository.connectToWebSocket(
                    wsUrl, onMessageReceived = { newMessage ->
                        processNewMessage(newMessage)
                    },
                    onError = { error ->
                        viewModelScope.launch {
                            _error.emit(error)
                        }
                    },
                    onStatusReceived = { orderId ->
                        processStatusUpdate(orderId)

                    })
            } catch (e: Exception) {
                Log.e("ChatVM", "Error: ${e.message}")
            }
        }
    }
    // Функция обновления статуса в списке
    private fun processStatusUpdate(orderId: Long) {
        _messages.update { currentList ->
            currentList.map { msg ->
                // Если сообщение относится к этому заказу, помечаем прочитанным
                if (msg.orderId == orderId) {
                    msg.copy(isRead = true)
                } else {
                    msg
                }
            }
        }
        Log.d("init chat history", "${_messages.value}")
    }


    fun sendText(orderId: Long, senderId: Long, receiverId: Long, content: String) {
        val msg = Message(
            orderId = orderId,
            chatId = "${orderId}_chat",
            senderId = senderId,
            receiverId = receiverId,
            content = content
        )
        repository.sendMessage(msg)
//        _messages.update { currentList -> currentList + msg }
    }

    fun sendImage(orderId: Long, senderId: Long, receiverId: Long, file: File) {
        viewModelScope.launch {
            val response = repository.uploadImage(file)
            if (response.isSuccessful) {
                val url = response.body()?.get("url") ?: ""
                val msg = Message(
                    orderId = orderId,
                    chatId = "${orderId}_chat",
                    senderId = senderId,
                    receiverId = receiverId,
                    imageUrl = url
                )
                repository.sendMessage(msg)
                _messages.value = _messages.value + msg
            }
        }
    }

    fun markMessagesAsRead(orderId: Long, userId: Long, receiverId: Long) {
        viewModelScope.launch {
            Log.d("markMessagesAsRead","start")
            repository.sendReadStatus(orderId, userId, receiverId)
        }
    }

    private fun processNewMessage(newMessage: Message) {
        viewModelScope.launch {
            _messages.update { currentList ->
                // Проверка на дубликаты (чтобы не добавлять сообщение, которое мы сами отправили и уже добавили в список)
                if (currentList.any { it.id == newMessage.id && it.id != 0L }) {
                    currentList
                } else {
                    currentList + newMessage
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}