package com.example.farmer.chat

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage

class ChatStompManager(private val userId: Long) {
    private var mStompClient: StompClient? = null
    private val gson = Gson()
    private var isConnected = false
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5

    @SuppressLint("CheckResult")
    fun connect(
        url: String,
        onMessageReceived: (Message) -> Unit,
        onConnected: () -> Unit = {},
        onError: (String) -> Unit,
        onStatusReceived: (Long) -> Unit
    ) {
        Log.d("STOMP", "Подключение к $url с userId=$userId")
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

        mStompClient?.lifecycle()?.subscribe({ lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("STOMP", "!---Соединение открыто---!")
                    isConnected = true
                    reconnectAttempts = 0

                    val personalTopic = "/queue/messages/$userId"
                    Log.d("STOMP", "Подписываюсь на $personalTopic")

                    mStompClient?.topic(personalTopic)?.subscribe({ stompMessage ->
                        Log.d("STOMP", "!---Получено сообщение: ${stompMessage.payload}---!")
                        try {
                            if (stompMessage.payload.contains("\"status\":\"READ_ALL\"")) {
                                val statusData = gson.fromJson(stompMessage.payload, Map::class.java)
                                // Gson парсит числа как Double, переводим в Long
                                val orderId = (statusData["orderId"] as? Double)?.toLong() ?: 0L

                                android.os.Handler(android.os.Looper.getMainLooper()).post {
                                    onStatusReceived(orderId)
                                }
                            } else {
                                val message =
                                    gson.fromJson(stompMessage.payload, Message::class.java)
                                android.os.Handler(android.os.Looper.getMainLooper()).post {
                                    onMessageReceived(message)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("STOMP", "Ошибка парсинга сообщения: ${e.message}")
                            Log.e("STOMP", "Payload: ${stompMessage.payload}")
                            onError("Ошибка получения сообщения")
                        }
                    }, { error ->
                        Log.e("STOMP", "Ошибка подписки на топик: ${error.message}")
                    })

                    // Уведомляем о подключении
                    onConnected()
                }

                LifecycleEvent.Type.CLOSED -> {
                    Log.d("STOMP", "!---Соединение закрыто---!")
                    isConnected = false
                }

                LifecycleEvent.Type.ERROR -> {
                    Log.e("STOMP", "!---Ошибка сокета---!", lifecycleEvent.exception)
                    isConnected = false
                    onError("Ошибка сокета")

                    // Попытка переподключения
                    if (reconnectAttempts < maxReconnectAttempts) {
                        reconnectAttempts++
                        Log.d(
                            "STOMP",
                            "Попытка переподключения $reconnectAttempts/$maxReconnectAttempts"
                        )
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            connect(url, onMessageReceived, onConnected, onError, onStatusReceived)
                        }, (3000 * reconnectAttempts).toLong())
                    }
                }

                else -> {
                    Log.d("STOMP", "Другое событие: ${lifecycleEvent.type}")
                }
            }
        }, { error ->
            Log.e("STOMP", "Ошибка в lifecycle: ${error.message}")
        })

        // Для проверки подключения
        mStompClient?.withClientHeartbeat(10000)
        mStompClient?.withServerHeartbeat(10000)


        Log.d("STOMP", "!---Вызываю connect()---!")
        mStompClient?.connect()
    }

    @SuppressLint("CheckResult")
    fun sendMessage(message: Message) {
        if (!isConnected) {
            Log.e("STOMP", "Нельзя отправить сообщение: соединение не установлено")
            return
        }

        try {
            val json = gson.toJson(message)
            Log.d("STOMP", "Отправка сообщения: $json")

            mStompClient?.send("/app/chat.send", json)?.subscribe({
                Log.d("STOMP", "Сообщение отправлено успешно")
            }, { throwable ->
                Log.e("STOMP", "Ошибка отправки сообщения: ${throwable.message}")
                throwable.printStackTrace()
            })
        } catch (e: Exception) {
            Log.e("STOMP", "Ошибка при отправке: ${e.message}")
        }
    }

    @SuppressLint("CheckResult")
    fun sendReadStatus(orderId: Long, userId: Long, receiverId: Long) {
        if (!isConnected) return

        val data = mapOf(
            "orderId" to orderId,
            "userId" to userId,
            "receiverId" to receiverId
        )
        val json = gson.toJson(data)

        // Добавляем пустую подписку .subscribe(), чтобы запрос ушел
        mStompClient?.send("/app/chat.readAll", json)?.subscribe({
            Log.d("STOMP", "✅ Сигнал о прочтении успешно ушел на сервер")
        }, { throwable ->
            Log.e("STOMP", "❌ Ошибка отправки статуса прочтения: ${throwable.message}")
        })
    }

    fun isConnected(): Boolean = isConnected

    fun disconnect() {
        try {
            mStompClient?.disconnect()
            isConnected = false
            Log.d("STOMP", "Отключено")
        } catch (e: Exception) {
            Log.e("STOMP", "Ошибка при отключении: ${e.message}")
        }
    }
}