package com.example.farmer.customer.data.network.model

import android.os.Parcelable
import com.example.farmer.farmer.AddNewProductFragment
import com.example.farmer.farmer.network.User
import kotlinx.parcelize.Parcelize

data class ProductWithPosDto(
    // Данные товара
    val productId: Long,
    val productName: String,
    val price: Double,
    val farmName: String, // Название фермы или точки
    val description: String?,
    val image: String?,

    // Данные точки продажи
    val posName: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

@Parcelize
data class Product(
    val id: Long,
    val name: String,
    val priceText: String,
    val farmName: String,
    val description: String?,
    val images: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val posName: String
) : Parcelable

fun ProductWithPosDto.toUI(): Product {
    return Product(
        id = this.productId,
        name = this.productName,
        priceText = "${this.price}₽",
        farmName = this.farmName,
        description = this.description,
        images = this.image,
        latitude = this.latitude,
        longitude = this.longitude,
        address = this.address,
        posName = this.posName
    )
}
