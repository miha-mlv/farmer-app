package com.example.farmer.repository

import com.example.farmer.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ProductRepository: JpaRepository<Product, Long> {
    fun findAllByFarmerId(farmerId: Long): List<Product>
    @Query("SELECT p.images FROM Product p WHERE p.id = :productId")
    fun getProductImages(@Param("productId") productId: Long): List<String>
}