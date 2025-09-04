package com.mytheresa.product_catalog.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    val sku: String,
    val price: BigDecimal,
    val description: String,
    val category: String,
)
