package com.mytheresa.product_catalog.service

import com.mytheresa.product_catalog.config.DiscountProperties
import com.mytheresa.product_catalog.domain.model.Product
import org.springframework.stereotype.Service

@Service
class DiscountService(
    private val discountProperties: DiscountProperties
) {
    
    fun calculateDiscount(product: Product): Int {

        return discountProperties.rules
            .filter { rule -> isRuleApplicable(rule, product) }
            .maxOfOrNull { it.percentage }
            ?: 0
    }
    
    private fun isRuleApplicable(rule: DiscountProperties.DiscountRule, product: Product): Boolean {
        return when (rule.type) {
            DiscountProperties.RuleType.CATEGORY ->
                product.category.value.equals(rule.condition, ignoreCase = true)
                
            DiscountProperties.RuleType.SKU_PATTERN ->
                product.sku.value.matches(Regex(rule.condition))
        }
    }
}
