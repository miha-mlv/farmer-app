package com.example.farmer.service

import com.example.farmer.dto.OrderItemDto
import com.example.farmer.dto.OrderRequest
import com.example.farmer.dto.OrderResponse
import com.example.farmer.dto.ProductRequest
import com.example.farmer.entity.Order
import com.example.farmer.entity.OrderItem
import com.example.farmer.repository.OrderRepository
import com.example.farmer.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val jwtService: JwtService,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
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

                // 3. TODO: Тут отправляем Push-уведомление фермеру (farmerId)
            }

        return createdOrders
    }

    fun getOrdersCustomer(customerId: Long): List<OrderResponse>{
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
                    OrderItemDto(item.productId, item.name, item.price, item.quantity)
                },
                rejectionReason = order.rejectionReason?.text,
                rejectionComment = order.rejectionComment,
                rejectedAt = order.rejectedAt
            )
        }
    }

    fun getOrdersFarmer(farmerId: Long): List<OrderResponse>{
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
                    OrderItemDto(item.productId, item.name, item.price, item.quantity)
                },
                rejectionReason = order.rejectionReason?.text,
                rejectionComment = order.rejectionComment,
                rejectedAt = order.rejectedAt
            )
        }
    }
}