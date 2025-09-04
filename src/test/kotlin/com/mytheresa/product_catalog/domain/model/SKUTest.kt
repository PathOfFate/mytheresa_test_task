package com.mytheresa.product_catalog.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SKUTest {

    @Test
    fun `should create SKU when value is not blank`() {
        val sku = SKU("PROD-001")
        assertEquals("PROD-001", sku.value)
    }

    @Test
    fun `should throw exception when SKU is blank`() {
        val exception = assertThrows<IllegalArgumentException> {
            SKU("")
        }
        assertEquals("SKU cannot be blank", exception.message)
    }

    @Test
    fun `should throw exception when SKU is whitespace`() {
        val exception = assertThrows<IllegalArgumentException> {
            SKU("   ")
        }
        assertEquals("SKU cannot be blank", exception.message)
    }
}
