package com.mytheresa.product_catalog.controller

import com.mytheresa.product_catalog.dto.ProductResponseDto
import com.mytheresa.product_catalog.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product catalog management API")
class ProductController(
    private val productService: ProductService
) {
    
    @GetMapping
    @Operation(
        summary = "Get products",
        description = "Retrieve products with optional filtering and sorting. Pagination is always enabled."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved products",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = ProductResponseDto::class))
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    fun getProducts(
        @Parameter(
            description = "Filter products by category",
            example = "Electronics",
        )
        @RequestParam(required = false) 
        category: String?,
        
        @Parameter(
            description = "Sort field (default value: SKU)",
            schema = Schema(
                allowableValues = ["SKU", "PRICE", "DESCRIPTION", "CATEGORY"],
                example = "SKU",
            )
        )
        @RequestParam(required = false, defaultValue = "SKU")
        sortBy: String,
        
        @Parameter(
            description = "Sort direction (default value: ASC)",
            schema = Schema(
                allowableValues = ["ASC", "DESC"],
                example = "ASC",
            )
        )
        @RequestParam(required = false, defaultValue = "ASC")
        sortDirection: String,
        
        @Parameter(description = "Page number (start from 0)")
        @RequestParam(required = false, defaultValue = "0")
        pageNumber: Int,
        
        @Parameter(description = "Page size (1-100)")
        @RequestParam(required = false, defaultValue = "20")
        pageSize: Int
    ): ResponseEntity<Page<ProductResponseDto>> {
        
        val products = productService.getProducts(
            category = category,
            sortBy = sortBy,
            sortDirection = sortDirection,
            pageNumber = pageNumber,
            pageSize = pageSize
        )
        
        return ResponseEntity.ok(products)
    }
}