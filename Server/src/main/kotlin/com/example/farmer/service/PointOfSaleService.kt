package com.example.farmer.service

import com.example.farmer.dto.PointOfSaleRequest
import com.example.farmer.dto.PosStatusRequest
import com.example.farmer.entity.PointOfSale
import com.example.farmer.repository.PointOfSaleRepository
import com.example.farmer.repository.ProductRepository
import com.example.farmer.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PointOfSaleService(
    private val posRepository: PointOfSaleRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    // Сохранение точки продажи
    @Transactional
    fun createPointOfSale(request: PointOfSaleRequest, token: String): PointOfSale {
        val farmer = userRepository.findByEmail(jwtService.extractEmail(token)!!)

        val pos = PointOfSale(
            name = request.name,
            address = request.address,
            latitude = request.latitude,
            longitude = request.longitude,
            farmer = farmer.get(),
            isActive = false
        )
        return posRepository.save(pos)
    }


    // Получение списка точек продаж фермера
    fun getFarmerPoints(token: String): List<PointOfSale> {
        val farmer = userRepository.findByEmail(jwtService.extractEmail(token)!!)
        return posRepository.findAllByFarmer(farmer.get())
    }

    // Обновление статуса и списка товаров
    @Transactional
    fun updateStatusAndProducts(posId: Long, request: PosStatusRequest, userEmail: String): PointOfSale {
        val pos = posRepository.findById(posId)
            .orElseThrow { NoSuchElementException("Точка с ID $posId не найдена") }

        // Проверка безопасности: только владелец может менять данные
        if (pos.farmer.email != userEmail) {
            throw IllegalAccessException("У вас нет прав для изменения этой точки")
        }

        pos.isActive = request.isActive

        // Если точка активируется, привязываем товары
        if (request.isActive && request.productIds != null) {
            val products = productRepository.findAllById(request.productIds)
            // Обновляем Many-to-Many связь
            pos.products = products.toMutableSet()
        }

        return posRepository.save(pos)
    }

    @Transactional
    fun deletePOS(id: Long, token: String){
        val pos = posRepository.findById(id).orElseThrow()
        posRepository.delete(pos)
    }


}