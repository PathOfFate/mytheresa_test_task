package com.mytheresa.product_catalog.service

import com.mytheresa.product_catalog.config.DiscountRules
import com.mytheresa.product_catalog.domain.model.Product
import org.springframework.stereotype.Service

@Service
class DiscountService(
    private val discountRules: DiscountRules
) {
    
    fun calculateDiscount(product: Product): Int {

        return discountRules.rules
            .filter { rule -> isRuleApplicable(rule, product) }
            .maxOfOrNull { it.percentage }
            ?: 0
    }
    
    private fun isRuleApplicable(rule: DiscountRules.DiscountRule, product: Product): Boolean {
        return when (rule.type) {
            DiscountRules.RuleType.CATEGORY ->
                product.category.value.equals(rule.condition, ignoreCase = true)
                
            DiscountRules.RuleType.SKU_PATTERN ->
                product.sku.value.matches(Regex(rule.condition))
        }
    }
}
