package com.mytheresa.product_catalog

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import com.mytheresa.product_catalog.config.DiscountProperties

@SpringBootApplication
@EnableConfigurationProperties(DiscountProperties::class)
@OpenAPIDefinition(
    info = Info(
        title = "Product Catalog API",
        description = """
            REST API for managing product catalog with discount calculation.
            
            ## Features
            - List products with automatic discount calculation
            - Filter products by category
            - Sort products by SKU, Price, Description, or Category
            - Pagination support
            
            ## Discount Rules
            - Electronics: 15% discount
            - Home & Kitchen: 25% discount
            - SKUs ending in '5': 30% discount
            - Only the highest discount is applied
        """,
    )
)
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
