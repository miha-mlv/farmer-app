package com.example.farmer.service

import com.example.farmer.dto.OrderItemDto
import com.example.farmer.dto.OrderRequest
import com.example.farmer.dto.OrderResponse
import com.example.farmer.dto.OrderUpdateRequest
import com.example.farmer.entity.Order
import com.example.farmer.entity.OrderItem
import com.example.farmer.entity.OrderStatus
import com.example.farmer.repository.OrderRepository
import com.example.farmer.repository.ProductRepository
import com.example.farmer.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val jwtService: JwtService,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val fcmService: FCMService
) {

    @Transactional
    fun createOrders(request: OrderRequest): List<Order> {
        val productsByFarmer = request.products.groupBy { it.farmerId }
        val createdOrders = mutableListOf<Order>()
        val customerId = jwtService.extractUserId(token = request.token)

        productsByFarmer.forEach { (farmerId, basketItems) ->
            val orderItems = basketItems.map {
                OrderItem(
                    productId = it.id,
                    name = it.name,
                    price = it.priceText.dropLast(3).toInt(),
                    quantity = it.quantity
                )
            }

            val newOrder = Order(
                customerId = customerId!!,
                farmerId = farmerId,
                totalAmount = orderItems.sumOf { it.price * it.quantity },
                createdAt = System.currentTimeMillis(),
                products = orderItems
            )

            createdOrders.add(orderRepository.save(newOrder))

            val farmerTokenFCM = userRepository.findById(farmerId).get().fcmToken

            if (farmerTokenFCM!!.isNotEmpty()) {
                fcmService.sendNotification(
                    token = farmerTokenFCM,
                    title = "Новый заказ",
                    message = "Вам поступил новый заказ на сумму: ${newOrder.totalAmount}",
                    orderId = 0
                )
            }
        }

        return createdOrders
    }

    fun getOrdersCustomer(customerId: Long): List<OrderResponse> {
        val orders = orderRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId)

        return orders.map { order ->
            val farmer = userRepository.findById(order.farmerId).get()
            OrderResponse(
                id = order.id,
                farmerId = order.farmerId,
                farmerName = farmer.farmName!!,
                customerId = order.customerId,
                totalAmount = order.totalAmount,
                status = order.status,
                createdAt = order.createdAt,
                products = order.products.map { item ->
                    OrderItemDto(
                        item.productId, item.name, item.price, item.quantity,
                        productRepository.getProductImages(item.productId).getOrNull(0)
                    )
                },
                rejectionReason = order.rejectionReason?.text,
                rejectionComment = order.rejectionComment,
                rejectedAt = order.rejectedAt
            )
        }
    }

    fun getOrdersFarmer(farmerId: Long): List<OrderResponse> {
        val orders = orderRepository.findAllByFarmerIdOrderByCreatedAtDesc(farmerId)

        return orders.map { order ->
            val farmer = userRepository.findById(order.farmerId).get()
            OrderResponse(
                id = order.id,
                farmerId = order.farmerId,
                farmerName = farmer.farmName!!,
                customerId = order.customerId,
                totalAmount = order.totalAmount,
                status = order.status,
                createdAt = order.createdAt,
                products = order.products.map { item ->
                    OrderItemDto(
                        item.productId, item.name, item.price, item.quantity,
                        productRepository.getProductImages(item.productId).getOrNull(0)
                    )
                },
                rejectionReason = order.rejectionReason?.text,
                rejectionComment = order.rejectionComment,
                rejectedAt = order.rejectedAt
            )
        }
    }

    @Transactional
    fun updateOrderStatus(orderId: Long, orderUpdateRequest: OrderUpdateRequest) {
        val order = orderRepository.findById(orderId).orElseThrow {
            EntityNotFoundException("Заказ с id ${orderId} не найден")
        }
        if (orderUpdateRequest.status.name != OrderStatus.REJECTED.name) {
            order.apply {
                status = orderUpdateRequest.status
            }
        } else {
            order.apply {
                status = orderUpdateRequest.status
                rejectionReason = orderUpdateRequest.reason
                rejectionComment = orderUpdateRequest.comment
                rejectedAt = System.currentTimeMillis()
            }

        }

        val savedOrder = orderRepository.save(order)

        fcmService.sendNotification(userRepository.findById(order.customerId).get().fcmToken!!, "Заказ #FA-${order.id}", "Статус заказа обновлен",0)
    }


}