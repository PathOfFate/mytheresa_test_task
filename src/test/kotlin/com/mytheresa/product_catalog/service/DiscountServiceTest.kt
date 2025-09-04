package com.mytheresa.product_catalog.service

import com.mytheresa.product_catalog.config.DiscountProperties
import com.mytheresa.product_catalog.entity.Product
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class DiscountServiceTest {
    
    private val discountProperties = DiscountProperties().apply {
        rules = listOf(
            DiscountProperties.DiscountRule(
                name = "Electronics Category",
                type = DiscountProperties.RuleType.CATEGORY,
                condition = "Electronics",
                percentage = 15
            ),
            DiscountProperties.DiscountRule(
                name = "Home & Kitchen Category",
                type = DiscountProperties.RuleType.CATEGORY,
                condition = "Home & Kitchen",
                percentage = 25
            ),
            DiscountProperties.DiscountRule(
                name = "SKU Ending with 5",
                type = DiscountProperties.RuleType.SKU_PATTERN,
                condition = ".*5$",
                percentage = 30
            )
        )
    }
    
    private val discountService = DiscountService(discountProperties)
    
    @Test
    fun `apply 15 percent discount for Electronics category`() {
        // Given
        val product = Product(
            sku = "ELEC-001",
            price = BigDecimal("100.00"),
            description = "Test Electronics",
            category = "Electronics"
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = discountService.calculateFinalPrice(product.price, discount)
        
        // Then
        assert(discount == 15)
        assert(finalPrice == BigDecimal("85.00"))
    }
    
    @Test
    fun `apply 25 percent discount for Home & Kitchen category`() {
        // Given
        val product = Product(
            sku = "HOME-001",
            price = BigDecimal("100.00"),
            description = "Test Kitchen Item",
            category = "Home & Kitchen"
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = discountService.calculateFinalPrice(product.price, discount)
        
        // Then
        assert(discount == 25)
        assert(finalPrice == BigDecimal("75.00"))
    }
    
    @Test
    fun `apply 30 percent discount for SKU ending with 5`() {
        // Given
        val product = Product(
            sku = "TEST-005",
            price = BigDecimal("100.00"),
            description = "Test Product",
            category = "Other"
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = discountService.calculateFinalPrice(product.price, discount)
        
        // Then
        assert(discount == 30)
        assert(finalPrice == BigDecimal("70.00"))
    }
    
    @Test
    fun `apply highest discount when multiple conditions match`() {
        // Given - Electronics product with SKU ending in 5
        val product = Product(
            sku = "ELEC-005",
            price = BigDecimal("120.00"),
            description = "Electronics with special SKU",
            category = "Electronics"
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = discountService.calculateFinalPrice(product.price, discount)
        
        // Then - Should apply 30% (SKU) instead of 15% (Electronics)
        assert(discount == 30)
        assert(finalPrice == BigDecimal("84.00"))
    }
    
    @Test
    fun `not apply discount for other categories`() {
        // Given
        val product = Product(
            sku = "CLOTH-001",
            price = BigDecimal("50.00"),
            description = "T-Shirt",
            category = "Clothing"
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = discountService.calculateFinalPrice(product.price, discount)
        
        // Then
        assert(discount == 0)
        assert(finalPrice == BigDecimal("50.00"))
    }
    
    @Test
    fun `round final price to 2 decimal places`() {
        // Given
        val product = Product(
            sku = "ELEC-001",
            price = BigDecimal("19.99"),
            description = "Wireless Mouse",
            category = "Electronics"
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = discountService.calculateFinalPrice(product.price, discount)
        
        // Then
        assert(discount == 15)
        assert(finalPrice == BigDecimal("16.99"))
        assert(finalPrice.scale() == 2)
    }
}