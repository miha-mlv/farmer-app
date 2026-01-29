package com.example.farmer.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ProductRequest(
    @NotBlank(message = "Не указано имя товара")
    val name: String,
    @field:NotNull("Не выбрана категория товара")
    val category: ProductCategory,
    @NotBlank(message = "Не указана цена товара")
    val price: Double,
    @NotBlank(message = "Не указано количество товара")
    val quantity: Int,
    val description: String? = null,
    val farmerToken: String
)

data class TokenRequest(
    val token: String
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Double,
    val quantity: Int,
    val category: String,
    val imageUrls: List<String> // Если сервер возвращает ссылки на фото
)

data class ResponseFarmer(
    val name: String,
    val farmName: String,
    val email: String
)

enum class ProductCategory(val displayName: String) {
    VEGETABLES("Овощи"),
    FRUITS("Фрукты"),
    DAIRY("Молочные продукты"),
    MEAT("Мясо"),
    GRAINS("Зерновые"),
    OTHER("Другое")
}

