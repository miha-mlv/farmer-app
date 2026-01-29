package com.example.farmer.service

import com.example.farmer.dto.ProductRequest
import com.example.farmer.dto.ProductResponse
import com.example.farmer.dto.ResponseFarmer
import com.example.farmer.entity.Product
import com.example.farmer.repository.ProductRepository
import com.example.farmer.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {
    private val fileStorageService = FileStorageService()


    //Добавление нового продукта
    fun addProduct(productRequest: ProductRequest, images: List<MultipartFile>): Product {
        val farmer = userRepository.findByEmail(jwtService.extractEmail(productRequest.farmerToken).toString())
            .orElseThrow { RuntimeException("Фермер не найден") }

        val imagesNames = mutableListOf<String>()

        images.forEach { file ->
            if (!file.isEmpty) {
                val fileName = fileStorageService.saveFile(file)
                imagesNames.add(fileName)
            }
        }

        val product = Product(
            name = productRequest.name,
            category = productRequest.category,
            price = productRequest.price,
            quantity = productRequest.quantity,
            description = productRequest.description,
            isActive = true,
            images = imagesNames,
            farmer = farmer
        )

        return productRepository.save(product)
    }

    fun getProductByFarmer(token: String): List<Product>{
        val token = token
            .replace("Bearer ", "")
            .replace("\"", "")
            .trim()
        val farmer = userRepository.findByEmail(jwtService.extractEmail(token).toString())
        return productRepository.findAllByFarmerId(farmer.get().id!!)
    }

    fun getProfile(token: String): ResponseFarmer{
        val token = token
            .replace("Bearer ", "")
            .replace("\"", "")
            .trim()
        val farmer = userRepository.findByEmail(jwtService.extractEmail(token).toString()).get()
        return ResponseFarmer(
            name = farmer.username,
            farmName = farmer.farmName!!,
            email = farmer.email
        )
    }

}