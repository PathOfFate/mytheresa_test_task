package com.mytheresa.product_catalog.domain.model

import java.math.BigDecimal
import java.math.RoundingMode

data class Product(
    val sku: SKU,
    val price: Money,
    val description: String,
    val category: Category,
)

data class SKU(val value: String) {
    init {
        require(value.isNotBlank()) { "SKU cannot be blank" }
    }
}

data class Money(val amount: BigDecimal) {
    init {
        require(amount >= BigDecimal.ZERO) { "Price cannot be negative" }
    }

    fun applyDiscountPercent(percent: Int): Money {
        if (percent == 0) return this
        val multiplier = BigDecimal(100 - percent).divide(BigDecimal(100))
        return Money(amount.multiply(multiplier).setScale(2, RoundingMode.HALF_UP))
    }

}

data class Category(val value: String) {
    init {
        require(value.isNotBlank()) { "Category cannot be blank" }
    }
}
