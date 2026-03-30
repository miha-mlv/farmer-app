package com.example.farmer.customer.data.network.model

import android.os.Parcelable
import com.example.farmer.customer.data.local.entity.BasketItem
import kotlinx.parcelize.Parcelize

data class OrderRequest(
    val totalAmount: Int,
    val products: List<BasketItem>,
    val createdAt: Long = System.currentTimeMillis(),
    val token: String
)

data class BasketItem(
    val id: Long,
    val name: String,
    val priceText: String,
    val farmName: String,
    val imageUrl: String?,
    val quantity: Int = 1,
    val farmerId: Long
)

data class OrderResponse(
    val message: String,
    val count: Int,
    val orderIds: List<String>
)

data class Order(
    val orderId: String,
    val farmerId: Long,
    val products: List<BasketItem>,
    val status: OrderStatus = OrderStatus.PENDING,

    // Поля для отказа
    val rejectionReason: RejectionReason? = null,
    val rejectionComment: String? = null,
    val rejectedAt: Long? = null
)

enum class OrderStatus(val displayName: String, val colorHex: String) {
    PENDING("В ожидании", "#FFA500"), // Оранжевый
    ACCEPTED("Принят", "#4CAF50"),               // Зеленый
    REJECTED("Отклонен", "#F44336"),             // Красный
    COMPLETED("Завершен", "#2196F3"),            // Синий
    CANCELLED("Отменен покупателем", "#9E9E9E")  // Серый
}

enum class RejectionReason(val text: String) {
    OUT_OF_STOCK("Товара нет в наличии"),
    DONT_WORK_NOW("Продажи временно закрыты"),
    PRICE_MISMATCH("Некорректная цена"),
    OTHER("Другое (см. комментарий)")
}

data class CustomerProfileResponse(
    val customerEmail: String,
    val customerName: String
)

@Parcelize
data class OrderHistoryResponse(
    val id: Long,
    val farmerId: Long,
    val farmerName: String, // Добавим имя фермера для заголовка
    val customerId: Long,
    val totalAmount: Int,
    val status: OrderStatus,
    val createdAt: Long,
    val products: List<OrderItemDto>,

    // Поля для отказа
    val rejectionReason: String? = null,
    val rejectionComment: String? = null,
    val rejectedAt: Long? = null
) : Parcelable

@Parcelize
data class OrderItemDto(
    val productId: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String? = null
) : Parcelable