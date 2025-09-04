package com.mytheresa.product_catalog.service

import com.mytheresa.product_catalog.dto.ProductResponseDto
import com.mytheresa.product_catalog.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val discountService: DiscountService
) {
    fun getProducts(
        category: String?,
        sortBy: String,
        sortDirection: String,
        pageNumber: Int,
        pageSize: Int
    ): Page<ProductResponseDto> {
        if (pageNumber < 0) {
            throw IllegalArgumentException("Page number must be non-negative")
        }

        if (pageSize !in 1..100) {
            throw IllegalArgumentException("Page size must be between 1 and 100")
        }

        val sortField = try {
            SortField.valueOf(sortBy.uppercase())
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid sort field: $sortBy. Must be one of: ${SortField.entries.joinToString()}")
        }

        val sortDir = try {
            Sort.Direction.fromString(sortDirection.uppercase())
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid sort direction: $sortDirection. Must be ASC or DESC")
        }

        val sort = if (sortField == SortField.SKU) {
            Sort.by(sortDir, sortField.fieldName)
        } else {
            Sort.by(sortDir, sortField.fieldName)
                .and(Sort.by(Sort.Direction.ASC, SortField.SKU.fieldName))
        }
        val pageable = PageRequest.of(pageNumber, pageSize, sort)

        val productsPage = if (category != null) {
                productRepository.findByCategoryIgnoreCase(category, pageable)
            } else {
                productRepository.findAll(pageable)
            }

        return productsPage.map { product ->
            val discount = discountService.calculateDiscount(product)
            val finalPrice = discountService.calculateFinalPrice(product.price, discount)

            ProductResponseDto(
                sku = product.sku,
                description = product.description,
                category = product.category,
                price = product.price,
                discount = discount,
                finalPrice = finalPrice
            )
        }
    }

    enum class SortField(val fieldName: String) {
        SKU("sku"),
        PRICE("price"),
        DESCRIPTION("description"),
        CATEGORY("category")
    }
}
