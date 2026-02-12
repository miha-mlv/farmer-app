package com.example.farmer.customer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.farmer.customer.data.local.entity.BasketItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BasketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToBasket(item: BasketItem)

    @Query("SELECT * FROM basket_products")
    fun getAllBasketItems(): Flow<List<BasketItem>>

    @Query("DELETE FROM basket_products WHERE id = :productId")
    suspend fun removeFromBasket(productId: Long)

    @Query("SELECT SUM(quantity) FROM basket_products")
    fun getBasketCount(): Flow<Int>
}