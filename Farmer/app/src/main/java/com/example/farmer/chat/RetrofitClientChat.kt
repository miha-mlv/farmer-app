package com.example.farmer.chat

import com.example.farmer.customer.data.network.CustomerApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientChat {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instance: ChatApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApi::class.java)
    }
}