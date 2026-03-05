package com.example.farmer.customer.data.repository

import com.example.farmer.customer.data.network.CustomerApi
import com.example.farmer.customer.data.network.model.CustomerProfileResponse
import com.example.farmer.customer.data.network.model.OrderHistoryResponse

class ProfileCustomerRepository(private val apiService: CustomerApi) {

    suspend fun getProfile(token: String): Result<CustomerProfileResponse>{
        return try{
            Result.success(apiService.getProfile(token))
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getMyOrders(token: String): Result<List<OrderHistoryResponse>>{
        return try{
            val response = apiService.getMyOrders(token)
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