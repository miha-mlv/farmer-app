package com.example.farmer.controller

import com.example.farmer.dto.ProductWithPosDto
import com.example.farmer.repository.PointOfSaleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customer")
class CustomerController(private val posRepository: PointOfSaleRepository) {

    @GetMapping("/products")
    fun getProducts(
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) minPrice: Double?,
        @RequestParam(required = false) maxPrice: Double?,
        @PageableDefault(size = 20) pageable: Pageable
    ): Page<ProductWithPosDto> {
        return posRepository.findActiveProducts(
            category = category,
            minPrice = minPrice,
            maxPrice = maxPrice,
            pageable = pageable
        )
    }

}