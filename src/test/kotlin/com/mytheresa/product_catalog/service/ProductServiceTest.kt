package com.mytheresa.product_catalog.service

import com.mytheresa.product_catalog.config.DiscountProperties
import com.mytheresa.product_catalog.entity.Product
import com.mytheresa.product_catalog.repository.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal

class ProductServiceTest {
    
    lateinit var productRepository: ProductRepository
    lateinit var productService: ProductService
    
    @BeforeEach
    fun setUp() {
        productRepository = mockk()
        val discountProperties = DiscountProperties().apply {
            rules = listOf(
                DiscountProperties.DiscountRule(
                    name = "Electronics Category",
                    type = DiscountProperties.RuleType.CATEGORY,
                    condition = "Electronics",
                    percentage = 15
                ),
                DiscountProperties.DiscountRule(
                    name = "Home & Kitchen Category",
                    type = DiscountProperties.RuleType.CATEGORY,
                    condition = "Home & Kitchen",
                    percentage = 25
                ),
                DiscountProperties.DiscountRule(
                    name = "SKU Ending with 5",
                    type = DiscountProperties.RuleType.SKU_PATTERN,
                    condition = ".*5$",
                    percentage = 30
                )
            )
        }
        val discountService = DiscountService(discountProperties)
        productService = ProductService(productRepository, discountService)
    }
    
    @Test
    fun `return all products without filter`() {
        // Given
        val products = listOf(
            Product("ELEC-001", BigDecimal("100.00"), "Test Electronics", "Electronics"),
            Product("HOME-001", BigDecimal("50.00"), "Test Home", "Home & Kitchen"),
            Product("CLOTH-001", BigDecimal("25.00"), "Test Clothing", "Clothing")
        )
        val pageable = PageRequest.of(0, 10)
        val sortedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "sku"))
        val page = PageImpl(products, sortedPageable, products.size.toLong())
        
        every { productRepository.findAll(sortedPageable) } returns page
        
        // When
        val result = productService.getProducts(
            category = null,
            sortBy = "SKU",
            sortDirection = "ASC",
            pageNumber = 0,
            pageSize = 10
        )
        
        // Then
        assertEquals(3L, result.totalElements)
        assertEquals(3, result.content.size)
        verify { productRepository.findAll(sortedPageable) }
    }
    
    @Test
    fun `filter products by category`() {
        // Given
        val electronics = listOf(
            Product("ELEC-001", BigDecimal("99.99"), "Smart TV", "Electronics"),
            Product("ELEC-002", BigDecimal("149.99"), "Laptop", "Electronics")
        )
        val pageable = PageRequest.of(0, 10)
        val sortedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "sku"))
        val page = PageImpl(electronics, sortedPageable, electronics.size.toLong())
        
        every { productRepository.findByCategoryIgnoreCase("Electronics", sortedPageable) } returns page
        
        // When
        val result = productService.getProducts(
            category = "Electronics",
            sortBy = "SKU",
            sortDirection = "ASC",
            pageNumber = 0,
            pageSize = 10
        )
        
        // Then
        assertEquals(2L, result.totalElements)
        
        result.content.forEach { product ->
            assertEquals("Electronics", product.category)
        }
        
        verify { productRepository.findByCategoryIgnoreCase("Electronics", sortedPageable) }
    }
    
    @Test
    fun `sort products by SKU ascending`() {
        // Given
        val products = listOf(
            Product("A-SKU", BigDecimal("50.00"), "Product A", "Home & Kitchen"),
            Product("B-SKU", BigDecimal("25.00"), "Product B", "Clothing"),
            Product("C-SKU", BigDecimal("100.00"), "Product C", "Electronics")
        )
        val pageable = PageRequest.of(0, 10)
        val sortedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "sku"))
        val page = PageImpl(products, sortedPageable, products.size.toLong())
        
        every { productRepository.findAll(sortedPageable) } returns page
        
        // When
        val result = productService.getProducts(
            category = null,
            sortBy = "SKU",
            sortDirection = "ASC",
            pageNumber = 0,
            pageSize = 10
        )
        
        // Then
        assertEquals("A-SKU", result.content[0].sku)
        assertEquals("B-SKU", result.content[1].sku)
        assertEquals("C-SKU", result.content[2].sku)
    }
    
    @Test
    fun `sort products by price descending`() {
        // Given
        val products = listOf(
            Product("HOME-001", BigDecimal("100.00"), "Blender", "Home & Kitchen"),
            Product("ELEC-001", BigDecimal("50.00"), "Headphones", "Electronics"),
            Product("CLOTH-001", BigDecimal("25.00"), "T-Shirt", "Clothing")
        )
        val pageable = PageRequest.of(0, 10)
        val sortedPageable = PageRequest.of(0, 10, 
            Sort.by(Sort.Direction.DESC, "price")
                .and(Sort.by(Sort.Direction.ASC, "sku"))
        )
        val page = PageImpl(products, sortedPageable, products.size.toLong())
        
        every { productRepository.findAll(sortedPageable) } returns page
        
        // When
        val result = productService.getProducts(
            category = null,
            sortBy = "price",
            sortDirection = "DESC",
            pageNumber = 0,
            pageSize = 10
        )
        
        // Then
        assertEquals(BigDecimal("100.00"), result.content[0].price)
        assertEquals(BigDecimal("50.00"), result.content[1].price)
        assertEquals(BigDecimal("25.00"), result.content[2].price)
    }
    
    @Test
    fun `apply correct discounts to products`() {
        // Given
        val products = listOf(
            Product("ELEC-001", BigDecimal("100.00"), "Electronics Item", "Electronics"),
            Product("HOME-001", BigDecimal("100.00"), "Kitchen Item", "Home & Kitchen"),
            Product("TEST-005", BigDecimal("100.00"), "Special SKU Item", "Other")
        )
        val pageable = PageRequest.of(0, 10)
        val sortedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "sku"))
        val page = PageImpl(products, sortedPageable, products.size.toLong())
        
        every { productRepository.findAll(sortedPageable) } returns page
        
        // When
        val result = productService.getProducts(
            category = null,
            sortBy = "SKU",
            sortDirection = "ASC",
            pageNumber = 0,
            pageSize = 10
        )
        
        // Then
        val electronicsProduct = result.content.find { it.sku == "ELEC-001" }
        assertNotNull(electronicsProduct)
        assertEquals(15, electronicsProduct!!.discount)
        assertEquals(BigDecimal("85.00"), electronicsProduct.finalPrice)
        
        val kitchenProduct = result.content.find { it.sku == "HOME-001" }
        assertNotNull(kitchenProduct)
        assertEquals(25, kitchenProduct!!.discount)
        assertEquals(BigDecimal("75.00"), kitchenProduct.finalPrice)
        
        val specialProduct = result.content.find { it.sku == "TEST-005" }
        assertNotNull(specialProduct)
        assertEquals(30, specialProduct!!.discount)
        assertEquals(BigDecimal("70.00"), specialProduct.finalPrice)
    }
    
    @Test
    fun `paginate results correctly`() {
        // Given
        val allProducts = (1..10).map { i ->
            Product("ELEC-${i.toString().padStart(3, '0')}", BigDecimal("${i * 10}.00"), "Product $i", "Electronics")
        }
        val pageable = PageRequest.of(0, 3)
        val sortedPageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "sku"))
        val page = PageImpl(allProducts.take(3), sortedPageable, allProducts.size.toLong())
        
        every { productRepository.findAll(sortedPageable) } returns page
        
        // When
        val result = productService.getProducts(
            category = null,
            sortBy = "SKU",
            sortDirection = "ASC",
            pageNumber = 0,
            pageSize = 3
        )
        
        // Then
        assertEquals(3, result.content.size)
        assertEquals(10L, result.totalElements)
        assertEquals(4, result.totalPages)
        assertEquals(0, result.number)
    }
    
    @Test
    fun `return empty page for out of bounds page request`() {
        // Given
        val pageable = PageRequest.of(10, 5)
        val sortedPageable = PageRequest.of(10, 5, Sort.by(Sort.Direction.ASC, "sku"))
        val emptyPage = PageImpl(emptyList<Product>(), sortedPageable, 2)
        
        every { productRepository.findAll(sortedPageable) } returns emptyPage
        
        // When
        val result = productService.getProducts(
            category = null,
            sortBy = "SKU",
            sortDirection = "ASC",
            pageNumber = 10,
            pageSize = 5
        )
        
        // Then
        assertTrue(result.content.isEmpty())
        assertEquals(2L, result.totalElements)
    }
    
    @Test
    fun `sort by description and filter simultaneously`() {
        // Given
        val products = listOf(
            Product("ELEC-002", BigDecimal("100.00"), "Alpha Product", "Electronics"),
            Product("ELEC-003", BigDecimal("75.00"), "Beta Product", "Electronics"),
            Product("ELEC-001", BigDecimal("50.00"), "Zebra Product", "Electronics")
        )
        val pageable = PageRequest.of(0, 10)
        val sortedPageable = PageRequest.of(0, 10, 
            Sort.by(Sort.Direction.ASC, "description")
                .and(Sort.by(Sort.Direction.ASC, "sku"))
        )
        val page = PageImpl(products, sortedPageable, products.size.toLong())
        
        every { productRepository.findByCategoryIgnoreCase("Electronics", sortedPageable) } returns page
        
        // When
        val result = productService.getProducts(
            category = "Electronics",
            sortBy = "description",
            sortDirection = "ASC",
            pageNumber = 0,
            pageSize = 10
        )
        
        // Then
        assertEquals(3L, result.totalElements)
        assertEquals("Alpha Product", result.content[0].description)
        assertEquals("Beta Product", result.content[1].description)
        assertEquals("Zebra Product", result.content[2].description)
    }
    
    @Test
    fun `ensure deterministic ordering when sorting by field with duplicate values`() {
        // Given products with duplicate prices
        val products = listOf(
            Product("PROD-003", BigDecimal("50.00"), "Product C", "Electronics"),
            Product("PROD-001", BigDecimal("50.00"), "Product A", "Electronics"),
            Product("PROD-002", BigDecimal("50.00"), "Product B", "Electronics"),
            Product("PROD-004", BigDecimal("75.00"), "Product D", "Electronics")
        )
        val pageable = PageRequest.of(0, 4)
        // Should sort by price DESC, then by SKU ASC for deterministic ordering
        val sortedPageable = PageRequest.of(0, 4, 
            Sort.by(Sort.Direction.DESC, "price")
                .and(Sort.by(Sort.Direction.ASC, "sku"))
        )
        val page = PageImpl(
            listOf(
                products[3], // PROD-004 (75.00)
                products[1], // PROD-001 (50.00) - sorted by SKU
                products[2], // PROD-002 (50.00) - sorted by SKU
                products[0]  // PROD-003 (50.00) - sorted by SKU
            ), 
            sortedPageable, 
            4
        )
        
        every { productRepository.findAll(sortedPageable) } returns page
        
        // When
        val result = productService.getProducts(
            category = null,
            sortBy = "price",
            sortDirection = "DESC",
            pageNumber = 0,
            pageSize = 4
        )
        
        // Then - verify products with same price are ordered by SKU
        assertEquals(4, result.content.size)
        assertEquals("PROD-004", result.content[0].sku)
        assertEquals(BigDecimal("75.00"), result.content[0].price)
        
        // Products with price 50.00 should be ordered by SKU
        assertEquals("PROD-001", result.content[1].sku)
        assertEquals(BigDecimal("50.00"), result.content[1].price)
        
        assertEquals("PROD-002", result.content[2].sku)
        assertEquals(BigDecimal("50.00"), result.content[2].price)
        
        assertEquals("PROD-003", result.content[3].sku)
        assertEquals(BigDecimal("50.00"), result.content[3].price)
    }
    
    @Test
    fun `ensure consistent pagination with duplicate values across pages`() {
        // Given products with duplicate categories
        val allProducts = listOf(
            Product("ELEC-001", BigDecimal("10.00"), "Product 1", "Electronics"),
            Product("ELEC-002", BigDecimal("20.00"), "Product 2", "Electronics"),
            Product("ELEC-003", BigDecimal("30.00"), "Product 3", "Electronics"),
            Product("HOME-001", BigDecimal("40.00"), "Product 4", "Home"),
            Product("HOME-002", BigDecimal("50.00"), "Product 5", "Home")
        )
        
        // First page request
        val page1Pageable = PageRequest.of(0, 2)
        val page1SortedPageable = PageRequest.of(0, 2,
            Sort.by(Sort.Direction.ASC, "category")
                .and(Sort.by(Sort.Direction.ASC, "sku"))
        )
        val page1 = PageImpl(
            listOf(allProducts[0], allProducts[1]), // ELEC-001, ELEC-002
            page1SortedPageable,
            5
        )
        
        every { productRepository.findAll(page1SortedPageable) } returns page1
        
        // When requesting first page
        val result1 = productService.getProducts(
            category = null,
            sortBy = "category",
            sortDirection = "ASC",
            pageNumber = 0,
            pageSize = 2
        )
        
        // Then
        assertEquals(2, result1.content.size)
        assertEquals("ELEC-001", result1.content[0].sku)
        assertEquals("ELEC-002", result1.content[1].sku)
        
        // Second page request
        val page2Pageable = PageRequest.of(1, 2)
        val page2SortedPageable = PageRequest.of(1, 2,
            Sort.by(Sort.Direction.ASC, "category")
                .and(Sort.by(Sort.Direction.ASC, "sku"))
        )
        val page2 = PageImpl(
            listOf(allProducts[2], allProducts[3]), // ELEC-003, HOME-001
            page2SortedPageable,
            5
        )
        
        every { productRepository.findAll(page2SortedPageable) } returns page2
        
        // When requesting second page
        val result2 = productService.getProducts(
            category = null,
            sortBy = "category",
            sortDirection = "ASC",
            pageNumber = 1,
            pageSize = 2
        )
        
        // Then - verify no overlap or missing items between pages
        assertEquals(2, result2.content.size)
        assertEquals("ELEC-003", result2.content[0].sku)
        assertEquals("HOME-001", result2.content[1].sku)
    }
}