package com.mytheresa.product_catalog.service

import com.mytheresa.product_catalog.config.DiscountProperties
import com.mytheresa.product_catalog.entity.Product
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class DiscountService(
    private val discountProperties: DiscountProperties
) {
    
    fun calculateDiscount(product: Product): Int {
        val applicableDiscounts = discountProperties.rules
            .filter { rule -> isRuleApplicable(rule, product) }
            .map { it.percentage }
        
        return applicableDiscounts.maxOrNull() ?: 0
    }
    
    private fun isRuleApplicable(rule: DiscountProperties.DiscountRule, product: Product): Boolean {
        return when (rule.type) {
            DiscountProperties.RuleType.CATEGORY -> 
                product.category.equals(rule.condition, ignoreCase = true)
                
            DiscountProperties.RuleType.SKU_PATTERN -> 
                product.sku.matches(Regex(rule.condition))
        }
    }
    
    fun calculateFinalPrice(originalPrice: BigDecimal, discountPercentage: Int): BigDecimal {
        if (discountPercentage == 0) {
            return originalPrice
        }
        
        val discountMultiplier = BigDecimal(100 - discountPercentage).divide(BigDecimal(100))
        return originalPrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP)
    }
}