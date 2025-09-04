package com.mytheresa.product_catalog.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "products")
data class Product(
    @Id
    @Column(name = "sku", nullable = false, unique = true)
    val sku: String,
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,
    
    @Column(name = "description", nullable = false, length = 500)
    val description: String,
    
    @Column(name = "category", nullable = false)
    val category: String,
)
