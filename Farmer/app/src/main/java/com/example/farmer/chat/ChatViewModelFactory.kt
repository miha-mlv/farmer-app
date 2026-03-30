package com.example.farmer.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.farmer.common.util.TokenManager

class ChatViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {

            // 1. Достаем TokenManager (или SharedPreferences)
            val tokenManager = TokenManager(context)
            val userId = tokenManager.getUserId() ?: 0L // Твой метод получения ID

            // 2. Собираем зависимости
            val apiService = RetrofitClientChat.instance
            val stompManager = ChatStompManager(userId)
            val repository = ChatRepository(apiService, stompManager)

            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}