package com.mytheresa.product_catalog.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class MoneyTest {

    @Test
    fun `should create Money when amount is positive`() {
        val money = Money(BigDecimal("100.00"))
        assertEquals(BigDecimal("100.00"), money.amount)
    }

    @Test
    fun `should create Money when amount is zero`() {
        val money = Money(BigDecimal.ZERO)
        assertEquals(BigDecimal.ZERO, money.amount)
    }

    @Test
    fun `should throw exception when amount is negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            Money(BigDecimal("-10.00"))
        }
        assertEquals("Price cannot be negative", exception.message)
    }

    @Test
    fun `should apply 0 percent discount correctly`() {
        val money = Money(BigDecimal("100.00"))
        val discounted = money.applyDiscountPercent(0)
        assertEquals(BigDecimal("100.00"), discounted.amount)
    }

    @Test
    fun `should apply 15 percent discount correctly`() {
        val money = Money(BigDecimal("100.00"))
        val discounted = money.applyDiscountPercent(15)
        assertEquals(BigDecimal("85.00"), discounted.amount)
    }
}
