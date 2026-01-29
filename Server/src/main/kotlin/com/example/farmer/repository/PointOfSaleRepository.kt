package com.example.farmer.repository

import com.example.farmer.entity.PointOfSale
import com.example.farmer.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PointOfSaleRepository: JpaRepository<PointOfSale, Long> {
    fun findAllByFarmer(farmer: User): List<PointOfSale>
}