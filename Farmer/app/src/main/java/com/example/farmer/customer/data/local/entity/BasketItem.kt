package com.example.farmer.customer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "basket_products")
data class BasketItem(
    @PrimaryKey val id: Long,
    val name: String,
    val priceText: String,
    val farmName: String,
    val imageUrl: String?,
    val quantity: Int = 1,
    val farmerId: Long
)