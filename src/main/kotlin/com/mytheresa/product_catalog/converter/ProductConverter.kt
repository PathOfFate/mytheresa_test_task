package com.mytheresa.product_catalog.converter

import com.mytheresa.product_catalog.domain.model.Category
import com.mytheresa.product_catalog.domain.model.Money
import com.mytheresa.product_catalog.domain.model.Product
import com.mytheresa.product_catalog.domain.model.SKU
import com.mytheresa.product_catalog.dto.ProductResponseDto
import com.mytheresa.product_catalog.entity.ProductEntity
import org.springframework.stereotype.Component

@Component
class ProductConverter {
    
    fun toDomain(entity: ProductEntity): Product {
        return Product(
            sku = SKU(entity.sku),
            price = Money(entity.price),
            description = entity.description,
            category = Category(entity.category),
        )
    }

    fun toDto(product: Product, discount: Int, finalPrice: Money): ProductResponseDto {
        return ProductResponseDto(
            sku = product.sku.value,
            description = product.description,
            category = product.category.value,
            price = product.price.amount,
            discount = discount,
            finalPrice = finalPrice.amount,
        )
    }
}
