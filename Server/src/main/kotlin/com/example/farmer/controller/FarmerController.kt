package com.example.farmer.controller

import com.example.farmer.dto.OrderResponse
import com.example.farmer.dto.PointOfSaleRequest
import com.example.farmer.dto.ProductRequest
import com.example.farmer.dto.ProductResponse
import com.example.farmer.dto.ResponseFarmer
import com.example.farmer.dto.TokenRequest
import com.example.farmer.entity.Product
import com.example.farmer.service.JwtService
import com.example.farmer.service.OrderService
import com.example.farmer.service.PointOfSaleService
import com.example.farmer.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/farmer/")
class FarmerController(
    private val productService: ProductService,
    private val jwtService: JwtService,
    private val orderService: OrderService
) {

    @PostMapping(
        value = ["/products/add"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun addProduct(
        @RequestPart("product_data") productRequest: ProductRequest,
        @RequestPart("images") images: List<MultipartFile>
    ): ResponseEntity<Any> {
        return try {
            if (images.size > 3) {
                return ResponseEntity.badRequest().body("Лимит изображений {3}")
            }
            val saveProduct = productService.addProduct(productRequest, images)
            ResponseEntity.status(HttpStatus.CREATED).body(saveProduct)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при добавлении товара: ${e.message}")
        }
    }

    @GetMapping("/products/my")
    fun getMyProduct(@RequestHeader("Authorization") token: String): ResponseEntity<List<Product>>{
        return try{
            ResponseEntity.ok(productService.getProductByFarmer(token))
        }catch (e: Exception){
            println("Ошибка при получении товаров: ${e.message}")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @GetMapping("/profile")
    fun getProfile(@RequestHeader("Authorization") token: String): ResponseEntity<ResponseFarmer>{
        return try{
            ResponseEntity.ok(productService.getProfile(token))
        }catch (e: Exception){
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @GetMapping("/my-orders")
    fun getMyOrders(@RequestHeader("Authorization") token: String): ResponseEntity<List<OrderResponse>> {
        val orders = orderService.getOrdersFarmer(jwtService.extractUserId(token)!!)
        return ResponseEntity.ok(orders)
    }

//    @PostMapping
//    fun updateOrderStatus(@RequestBody request: Order):

}