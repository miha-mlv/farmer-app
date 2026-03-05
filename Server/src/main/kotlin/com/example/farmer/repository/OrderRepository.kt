package com.example.farmer.repository

import com.example.farmer.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByFarmerId(farmerId: Long): List<Order>
    fun findAllByCustomerIdOrderByCreatedAtDesc(customerId: Long): List<Order>
    fun findAllByFarmerIdOrderByCreatedAtDesc(farmerId: Long): List<Order>
}