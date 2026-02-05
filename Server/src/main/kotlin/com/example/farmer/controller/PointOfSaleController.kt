package com.example.farmer.controller

import com.example.farmer.dto.PointOfSaleRequest
import com.example.farmer.dto.PosStatusRequest
import com.example.farmer.entity.PointOfSale
import com.example.farmer.service.PointOfSaleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/farmer")
class PointOfSaleController(private val posService: PointOfSaleService) {

    @PostMapping("/pos")
    fun create(
        @RequestBody request: PointOfSaleRequest,
        @RequestHeader("token") token: String
    ): ResponseEntity<PointOfSale> {
        if(token == null){
            return ResponseEntity.status(401).build()
        }
        return ResponseEntity.ok(posService.createPointOfSale(request, token))
    }

    @GetMapping("/pos/my")
    fun getMyPoints(@RequestHeader("token") token: String): ResponseEntity<List<PointOfSale>> {
        return ResponseEntity.ok(posService.getFarmerPoints(token))
    }

    @PatchMapping("/pos/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: PosStatusRequest,
        @RequestHeader("token")token: String
    ): ResponseEntity<PointOfSale> {
        return try {
            val updatedPos = posService.updateStatusAndProducts(id, request, token)
            ResponseEntity.ok(updatedPos)
        } catch (e: IllegalAccessException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping("/pos/{id}")
    fun delete(@PathVariable id: Long, @RequestHeader("token") token: String): ResponseEntity<Unit>{
        posService.deletePOS(id, token)
        return ResponseEntity.noContent().build()
    }

}