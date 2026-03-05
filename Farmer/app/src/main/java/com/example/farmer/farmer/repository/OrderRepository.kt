package com.example.farmer.farmer.repository

import com.example.farmer.customer.data.network.model.OrderHistoryResponse
import com.example.farmer.farmer.network.FarmerApi

class OrderRepository(private val api: FarmerApi) {

    suspend fun getMyOrders(token: String): Result<List<OrderHistoryResponse>>{
        return try{
            val response = api.getMyOrders(token)
            if(response.isSuccessful){
                Result.success(response.body()!!)
            }else{
                Result.failure(Exception("Ошибка сервера: ${response.code()}"))
            }
        }catch(e: Exception){
            Result.failure(e)
        }
    }
}