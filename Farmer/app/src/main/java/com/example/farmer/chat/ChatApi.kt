package com.example.farmer.chat

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ChatApi {
    @GET("/api/chat/{orderId}/history")
    suspend fun getChatHistory(@Path("orderId") orderId: Long): List<Message>

    @Multipart
    @POST("/api/chat/attachments/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<Map<String, String>>
}