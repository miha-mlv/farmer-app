package com.example.farmer.controller

import com.example.farmer.dto.CustomerProfileResponse
import com.example.farmer.dto.OrderRequest
import com.example.farmer.dto.OrderResponse
import com.example.farmer.dto.ProductCategory
import com.example.farmer.dto.ProductWithPosDto
import com.example.farmer.repository.PointOfSaleRepository
import com.example.farmer.repository.ProductRepository
import com.example.farmer.repository.UserRepository
import com.example.farmer.service.JwtService
import com.example.farmer.service.OrderService
import org.osgi.annotation.bundle.Headers
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customer")
class CustomerController(
    private val posRepository: PointOfSaleRepository,
    private val productRepository: ProductRepository,
    private val orderService: OrderService,
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    @GetMapping("/products")
    fun getProducts(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) minPrice: Double?,
        @RequestParam(required = false) maxPrice: Double?,
        @PageableDefault(size = 20) pageable: Pageable
    ): Page<ProductWithPosDto> {
        val categoryEnumName = ProductCategory.fromDisplayName(category)
        return posRepository.findActiveProducts(
            name = name,
            category = categoryEnumName,
            minPrice = minPrice,
            maxPrice = maxPrice,
            pageable = pageable
        )
    }

    @GetMapping("/products/{id}/images")
    fun getProductImages(@PathVariable id: Long): List<String> {
        return productRepository.getProductImages(id)
    }

    @PostMapping("/order")
    fun createOrder(@RequestBody request: OrderRequest): ResponseEntity<Any> {
        return try {
            val createdOrders = orderService.createOrders(request)
            ResponseEntity.ok(
                mapOf(
                    "message" to "Заказы успешно созданы",
                    "count" to createdOrders.size,
                    "orderIds" to createdOrders.map { "Order #FA-" + it.id }
                ))
        } catch (e: Exception) {
            ResponseEntity.status(400).body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/profile")
    fun getProfile(@RequestHeader("Authorization") token: String): CustomerProfileResponse{
        val user = userRepository.findByEmail(jwtService.extractEmail(token)!!).get()
        return CustomerProfileResponse(
            customerEmail = user.email,
            customerName = user.username
        )
    }

    @GetMapping("/my-orders")
    fun getMyOrders(@RequestHeader("Authorization") token: String): ResponseEntity<List<OrderResponse>> {
        val orders = orderService.getOrdersCustomer(jwtService.extractUserId(token)!!)
        return ResponseEntity.ok(orders)
    }

}