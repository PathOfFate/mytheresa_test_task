package com.mytheresa.product_catalog.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "discount")
class DiscountProperties {
    var rules: List<DiscountRule> = emptyList()
    
    data class DiscountRule(
        var name: String = "",
        var type: RuleType = RuleType.CATEGORY,
        var condition: String = "",
        var percentage: Int = 0
    )
    
    enum class RuleType {
        CATEGORY, 
        SKU_PATTERN
    }
}
