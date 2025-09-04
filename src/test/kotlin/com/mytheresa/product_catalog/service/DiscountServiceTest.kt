package com.mytheresa.product_catalog.service

import com.mytheresa.product_catalog.config.DiscountProperties
import com.mytheresa.product_catalog.domain.model.Category
import com.mytheresa.product_catalog.domain.model.Money
import com.mytheresa.product_catalog.domain.model.Product
import com.mytheresa.product_catalog.domain.model.SKU
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
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
            sku = SKU("ELEC-001"),
            price = Money(BigDecimal("100.00")),
            description = "Test Electronics",
            category = Category("Electronics"),
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = product.price.applyDiscountPercent(discount)
        
        // Then
        assertEquals(15, discount)
        assertEquals(BigDecimal("85.00"), finalPrice.amount)
    }
    
    @Test
    fun `apply 25 percent discount for Home & Kitchen category`() {
        // Given
        val product = Product(
            sku = SKU("HOME-001"),
            price = Money(BigDecimal("100.00")),
            description = "Test Kitchen Item",
            category = Category("Home & Kitchen"),
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = product.price.applyDiscountPercent(discount)
        
        // Then
        assertEquals(25, discount)
        assertEquals(BigDecimal("75.00"), finalPrice.amount)
    }
    
    @Test
    fun `apply 30 percent discount for SKU ending with 5`() {
        // Given
        val product = Product(
            sku = SKU("TEST-005"),
            price = Money(BigDecimal("100.00")),
            description = "Test Product",
            category = Category("Other"),
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = product.price.applyDiscountPercent(discount)
        
        // Then
        assertEquals(30, discount)
        assertEquals(BigDecimal("70.00"), finalPrice.amount)
    }
    
    @Test
    fun `apply highest discount when multiple conditions match`() {
        // Given - Electronics product with SKU ending in 5
        val product = Product(
            sku = SKU("ELEC-005"),
            price = Money(BigDecimal("120.00")),
            description = "Electronics with special SKU",
            category = Category("Electronics"),
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = product.price.applyDiscountPercent(discount)
        
        // Then - Should apply 30% (SKU) instead of 15% (Electronics)
        assertEquals(30, discount)
        assertEquals(BigDecimal("84.00"), finalPrice.amount)
    }
    
    @Test
    fun `not apply discount for other categories`() {
        // Given
        val product = Product(
            sku = SKU("CLOTH-001"),
            price = Money(BigDecimal("50.00")),
            description = "T-Shirt",
            category = Category("Clothing"),
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = product.price.applyDiscountPercent(discount)
        
        // Then
        assertEquals(0, discount)
        assertEquals(BigDecimal("50.00"), finalPrice.amount)
    }
    
    @Test
    fun `round final price to 2 decimal places`() {
        // Given
        val product = Product(
            sku = SKU("ELEC-001"),
            price = Money(BigDecimal("19.99")),
            description = "Wireless Mouse",
            category = Category("Electronics"),
        )
        
        // When
        val discount = discountService.calculateDiscount(product)
        val finalPrice = product.price.applyDiscountPercent(discount)
        
        // Then
        assertEquals(15, discount)
        assertEquals(BigDecimal("16.99"), finalPrice.amount)
        assertEquals(2, finalPrice.amount.scale())
    }
}
