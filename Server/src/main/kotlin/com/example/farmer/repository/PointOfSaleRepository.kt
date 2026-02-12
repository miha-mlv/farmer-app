package com.example.farmer.repository

import com.example.farmer.dto.ProductWithPosDto
import com.example.farmer.entity.PointOfSale
import com.example.farmer.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PointOfSaleRepository: JpaRepository<PointOfSale, Long> {
    fun findAllByFarmer(farmer: User): List<PointOfSale>

    @Query("""
    SELECT 
        p.id AS productId, 
        p.name AS productName, 
        p.price AS price, 
        pos.name AS farmName, 
        p.description AS description, 
        (SELECT MIN(img) FROM p.images img) AS mainImage,
        pos.name AS posName, 
        pos.address AS address, 
        pos.latitude AS latitude, 
        pos.longitude AS longitude
    FROM PointOfSale pos
    JOIN pos.products p
    WHERE pos.isActive = true
    AND (:category IS NULL OR p.category = :category)
    AND (:minPrice IS NULL OR p.price >= :minPrice)
    AND (:maxPrice IS NULL OR p.price <= :maxPrice)
""")
    fun findActiveProducts(
        category: String?,
        minPrice: Double?,
        maxPrice: Double?,
        pageable: Pageable
    ): Page<ProductWithPosDto>
}