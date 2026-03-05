package com.example.farmer.customer.data.repository

import com.example.farmer.customer.data.local.dao.BasketDao
import com.example.farmer.customer.data.local.entity.BasketItem
import com.example.farmer.customer.data.network.CustomerApi
import com.example.farmer.customer.data.network.model.OrderRequest
import com.example.farmer.customer.data.network.model.OrderResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class ProductBasketRepository(private val apiService: CustomerApi, private val basketDao: BasketDao) {

    // Работа с локальной корзиной
    suspend fun addToBasket(item: BasketItem) = basketDao.addToBasket(item)

    fun getBasketItems() = basketDao.getAllBasketItems()

    suspend fun removeFromBasket(id: Long) = basketDao.removeFromBasket(id)

    // Получаем количество товаров
    fun getBasketCount(): Flow<Int> = basketDao.getBasketCount()

    suspend fun clearBasket() = basketDao.clearBasket()

    suspend fun updateQuantity(id: Long, quantity: Int) = basketDao.updateQuantity(id, quantity)

    suspend fun sendOrder(request: OrderRequest): Result<OrderResponse>{
        return try{
            val response = apiService.postOrder(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Ошибка сервера: ${response.code()}"))
            }
        }catch(e: Exception){
            Result.failure(e)
        }
    }
}