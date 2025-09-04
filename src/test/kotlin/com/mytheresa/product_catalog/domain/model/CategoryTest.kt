package com.mytheresa.product_catalog.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CategoryTest {

    @Test
    fun `should create Category when value is not blank`() {
        val category = Category("Electronics")
        assertEquals("Electronics", category.value)
    }

    @Test
    fun `should throw exception when Category is blank`() {
        val exception = assertThrows<IllegalArgumentException> {
            Category("")
        }
        assertEquals("Category cannot be blank", exception.message)
    }

    @Test
    fun `should throw exception when Category is whitespace`() {
        val exception = assertThrows<IllegalArgumentException> {
            Category("   ")
        }
        assertEquals("Category cannot be blank", exception.message)
    }
}
