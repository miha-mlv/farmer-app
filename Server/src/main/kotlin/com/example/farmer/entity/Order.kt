package com.example.farmer.entity

import com.example.farmer.dto.OrderResponse
import jakarta.persistence.*

@Entity
@Table(name = "orders")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val farmerId: Long,
    val customerId: Long,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var products: List<OrderItem> = mutableListOf(),
    val totalAmount: Int,
    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long,

    // Поля для случая отказа
    var rejectionReason: RejectionReason? = null,
    var rejectionComment: String? = null,
    var rejectedAt: Long? = null // Timestamp
)