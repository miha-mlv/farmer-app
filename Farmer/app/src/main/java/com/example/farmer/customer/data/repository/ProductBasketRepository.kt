package com.example.farmer.customer.data.repository

import com.example.farmer.customer.data.local.dao.BasketDao
import com.example.farmer.customer.data.local.entity.BasketItem
import com.example.farmer.customer.data.network.CustomerApi
import kotlinx.coroutines.flow.Flow

class ProductBasketRepository(private val apiService: CustomerApi, private val basketDao: BasketDao) {

    // Работа с локальной корзиной
    suspend fun addToBasket(item: BasketItem) = basketDao.addToBasket(item)

    fun getBasketItems() = basketDao.getAllBasketItems()

    suspend fun removeFromBasket(id: Long) = basketDao.removeFromBasket(id)

    // Получаем количество товаров
    fun getBasketCount(): Flow<Int> = basketDao.getBasketCount()
}