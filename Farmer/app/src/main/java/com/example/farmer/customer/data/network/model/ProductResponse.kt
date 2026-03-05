package com.example.farmer.customer.data.network.model

data class ProductResponse(
    val content: List<ProductWithPosDto>,
    val page: PageInfo // Данные о пагинации теперь здесь
)

data class PageInfo(
    val size: Int,
    val number: Int,
    val totalElements: Long,
    val totalPages: Int
)