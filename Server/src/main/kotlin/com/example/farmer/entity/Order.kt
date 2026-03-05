package com.example.farmer.entity

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
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long,

    // Поля для случая отказа
    val rejectionReason: RejectionReason? = null,
    val rejectionComment: String? = null,
    val rejectedAt: Long? = null // Timestamp
)
