package com.example.farmer.farmer.network

import com.example.farmer.common.network.FarmerProfile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface FarmerApi {
    @Multipart
    @POST("/api/farmer/products/add")
    suspend fun createProduct(
        @Part("product_data") productData: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<Unit>

    @GET("/api/farmer/products/my")
    suspend fun getMyProducts(
        @Header("Authorization") request: String
    ): Response<List<Product>>

    @GET("/api/farmer/profile")
    suspend fun getProfile(
        @Header("Authorization") request: String
    ): Response<FarmerProfile>

}