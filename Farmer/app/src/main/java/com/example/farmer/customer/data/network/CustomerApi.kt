package com.example.farmer.customer.data.network

import com.example.farmer.customer.data.network.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CustomerApi {

    @GET("/api/customer/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("category") category: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null
    ): ProductResponse

}