package com.example.farmer.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "points_of_sale")
class PointOfSale(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    var isActive: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id")
    val farmer: User,

    @ManyToMany
    @JoinTable(
        name = "pos_products",
        joinColumns = [JoinColumn(name = "pos_id")],
        inverseJoinColumns = [JoinColumn(name = "product_id")]
    )
    var products: MutableSet<Product> = mutableSetOf()
)