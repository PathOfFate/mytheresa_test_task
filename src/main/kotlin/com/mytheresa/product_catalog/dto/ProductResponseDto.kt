package com.mytheresa.product_catalog.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Product response with discount information")
data class ProductResponseDto(
    @param:Schema(description = "Product SKU", example = "SKU0001")
    val sku: String,
    
    @param:Schema(description = "Product description", example = "Wireless Mouse with ergonomic design")
    val description: String,
    
    @param:Schema(description = "Product category", example = "Electronics")
    val category: String,
    
    @param:Schema(description = "Original price without discount", example = "19.99")
    val price: BigDecimal,
    
    @param:Schema(description = "Discount percentage (0 if no discount applies)", example = "15")
    val discount: Int = 0,
    
    @param:Schema(description = "Final price after discount", example = "16.99")
    val finalPrice: BigDecimal,
)
