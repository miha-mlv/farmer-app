package com.example.farmer.customer.data.network

import com.example.farmer.customer.data.network.model.CustomerProfileResponse
import com.example.farmer.customer.data.network.model.OrderHistoryResponse
import com.example.farmer.customer.data.network.model.OrderRequest
import com.example.farmer.customer.data.network.model.OrderResponse
import com.example.farmer.customer.data.network.model.ProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CustomerApi {

    @GET("/api/customer/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("name") name: String? = null,
        @Query("category") category: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null
    ): ProductResponse

    @GET("/api/customer/products/{id}/images")
    suspend fun productImages(@Path("id") id: Long): List<String>

    @POST("/api/customer/order")
    suspend fun postOrder(@Body request: OrderRequest) : Response<OrderResponse>

    @GET("/api/customer/profile")
    suspend fun getProfile(@Header("Authorization") token: String): CustomerProfileResponse

    @GET("/api/customer/my-orders")
    suspend fun getMyOrders(@Header("Authorization") token: String): Response<List<OrderHistoryResponse>>
}