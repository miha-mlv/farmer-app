package com.example.farmer.repository

import com.example.farmer.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ProductRepository: JpaRepository<Product, Long> {

    fun findAllByFarmerId(farmerId: Long): List<Product>
}