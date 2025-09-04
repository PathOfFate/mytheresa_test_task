package com.mytheresa.product_catalog.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "discount")
data class DiscountProperties(
    val rules: List<DiscountRule> = emptyList()
) {
    data class DiscountRule(
        val name: String,
        val type: RuleType,
        val condition: String,
        val percentage: Int,
    )
    
    enum class RuleType {
        CATEGORY, 
        SKU_PATTERN,
    }
}
