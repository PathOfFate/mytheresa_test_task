package com.mytheresa.product_catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {
    
    @Autowired
    lateinit var mockMvc: MockMvc
    
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should return all test products`() {
        val result = mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        assertEquals(7, content.size)
        
        // Products are sorted by default (SKU ASC), so order is:
        // CLOTH-001, CLOTH-002, ELEC-001, ELEC-005, HOME-001, HOME-002, OTHER-015
        
        // CLOTH-001
        assertEquals("CLOTH-001", content[0]["sku"])
        assertEquals("T-Shirt", content[0]["description"])
        assertEquals("Clothing", content[0]["category"])
        assertEquals(20.0, content[0]["price"])
        assertEquals(0, content[0]["discount"])
        assertEquals(20.0, content[0]["finalPrice"])
        
        // CLOTH-002
        assertEquals("CLOTH-002", content[1]["sku"])
        assertEquals("Jeans", content[1]["description"])
        assertEquals("Clothing", content[1]["category"])
        assertEquals(35.0, content[1]["price"])
        assertEquals(0, content[1]["discount"])
        assertEquals(35.0, content[1]["finalPrice"])
        
        // ELEC-001
        assertEquals("ELEC-001", content[2]["sku"])
        assertEquals("Smart TV", content[2]["description"])
        assertEquals("Electronics", content[2]["category"])
        assertEquals(99.99, content[2]["price"])
        assertEquals(15, content[2]["discount"])
        assertEquals(84.99, content[2]["finalPrice"])
        
        // ELEC-005
        assertEquals("ELEC-005", content[3]["sku"])
        assertEquals("Laptop", content[3]["description"])
        assertEquals("Electronics", content[3]["category"])
        assertEquals(150.0, content[3]["price"])
        assertEquals(30, content[3]["discount"])
        assertEquals(105.0, content[3]["finalPrice"])
        
        // HOME-001
        assertEquals("HOME-001", content[4]["sku"])
        assertEquals("Blender", content[4]["description"])
        assertEquals("Home & Kitchen", content[4]["category"])
        assertEquals(30.0, content[4]["price"])
        assertEquals(25, content[4]["discount"])
        assertEquals(22.5, content[4]["finalPrice"])
        
        // HOME-002
        assertEquals("HOME-002", content[5]["sku"])
        assertEquals("Toaster", content[5]["description"])
        assertEquals("Home & Kitchen", content[5]["category"])
        assertEquals(45.0, content[5]["price"])
        assertEquals(25, content[5]["discount"])
        assertEquals(33.75, content[5]["finalPrice"])
        
        // OTHER-015
        assertEquals("OTHER-015", content[6]["sku"])
        assertEquals("Special Item", content[6]["description"])
        assertEquals("Other", content[6]["category"])
        assertEquals(25.0, content[6]["price"])
        assertEquals(30, content[6]["discount"])
        assertEquals(17.5, content[6]["finalPrice"])
    }
    
    @Test
    fun `should filter products by Electronics category`() {
        val result = mockMvc.perform(get("/api/products?category=Electronics"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        assertEquals(2, content.size)
        
        // ELEC-001
        assertEquals("ELEC-001", content[0]["sku"])
        assertEquals("Smart TV", content[0]["description"])
        assertEquals("Electronics", content[0]["category"])
        assertEquals(99.99, content[0]["price"])
        assertEquals(15, content[0]["discount"])
        assertEquals(84.99, content[0]["finalPrice"])
        
        // ELEC-005
        assertEquals("ELEC-005", content[1]["sku"])
        assertEquals("Laptop", content[1]["description"])
        assertEquals("Electronics", content[1]["category"])
        assertEquals(150.0, content[1]["price"])
        assertEquals(30, content[1]["discount"])
        assertEquals(105.0, content[1]["finalPrice"])
    }
    
    @Test
    fun `should filter products by Home & Kitchen category`() {
        val result = mockMvc.perform(get("/api/products")
            .param("category", "Home & Kitchen"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        assertEquals(2, content.size)
        
        // HOME-001
        assertEquals("HOME-001", content[0]["sku"])
        assertEquals("Blender", content[0]["description"])
        assertEquals("Home & Kitchen", content[0]["category"])
        assertEquals(30.0, content[0]["price"])
        assertEquals(25, content[0]["discount"])
        assertEquals(22.5, content[0]["finalPrice"])
        
        // HOME-002
        assertEquals("HOME-002", content[1]["sku"])
        assertEquals("Toaster", content[1]["description"])
        assertEquals("Home & Kitchen", content[1]["category"])
        assertEquals(45.0, content[1]["price"])
        assertEquals(25, content[1]["discount"])
        assertEquals(33.75, content[1]["finalPrice"])
    }
    
    @Test
    fun `should apply highest discount when multiple conditions match`() {
        val result = mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        
        // ELEC-005 is Electronics (15%) but ends with 5 (30%) - should apply 30%
        assertEquals("ELEC-005", content[3]["sku"])
        assertEquals("Electronics", content[3]["category"])
        assertEquals(150.0, content[3]["price"])
        assertEquals(30, content[3]["discount"])
        assertEquals(105.0, content[3]["finalPrice"])
    }
    
    @Test
    fun `should sort products by SKU ascending`() {
        val result = mockMvc.perform(get("/api/products?sortBy=SKU&sortDirection=ASC"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        assertEquals(7, content.size)
        
        // Check sort order
        assertEquals("CLOTH-001", content[0]["sku"])
        assertEquals("CLOTH-002", content[1]["sku"])
        assertEquals("ELEC-001", content[2]["sku"])
        assertEquals("ELEC-005", content[3]["sku"])
        assertEquals("HOME-001", content[4]["sku"])
        assertEquals("HOME-002", content[5]["sku"])
        assertEquals("OTHER-015", content[6]["sku"])
    }
    
    @Test
    fun `should sort products by price descending`() {
        val result = mockMvc.perform(get("/api/products?sortBy=price&sortDirection=DESC"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        
        // ELEC-005 has highest price (150.00)
        assertEquals("ELEC-005", content[0]["sku"])
        assertEquals(150.0, content[0]["price"])
        
        // Then ELEC-001 (99.99)
        assertEquals("ELEC-001", content[1]["sku"])
        assertEquals(99.99, content[1]["price"])
        
        // Then HOME-002 (45.00)
        assertEquals("HOME-002", content[2]["sku"])
        assertEquals(45.0, content[2]["price"])
    }
    
    @Test
    fun `should sort products by category ascending`() {
        val result = mockMvc.perform(get("/api/products?sortBy=category&sortDirection=ASC"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        
        // First two should be Clothing
        assertEquals("Clothing", content[0]["category"])
        assertEquals("Clothing", content[1]["category"])
        
        // Next two should be Electronics
        assertEquals("Electronics", content[2]["category"])
        assertEquals("Electronics", content[3]["category"])
        
        // Next two should be Home & Kitchen
        assertEquals("Home & Kitchen", content[4]["category"])
        assertEquals("Home & Kitchen", content[5]["category"])
        
        // Last should be Other
        assertEquals("Other", content[6]["category"])
    }
    
    @Test
    fun `should paginate results`() {
        val result = mockMvc.perform(get("/api/products?paginated=true&pageNumber=0&pageSize=3"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        assertEquals(3, content.size)
        
        // First page should have CLOTH-001, CLOTH-002, ELEC-001
        assertEquals("CLOTH-001", content[0]["sku"])
        assertEquals("CLOTH-002", content[1]["sku"])
        assertEquals("ELEC-001", content[2]["sku"])
        
        val totalElements = response["totalElements"] as Int
        assertEquals(7, totalElements)
        
        val totalPages = response["totalPages"] as Int
        assertEquals(3, totalPages)
    }
    
    @Test
    fun `should return second page of results`() {
        val result = mockMvc.perform(get("/api/products?paginated=true&pageNumber=1&pageSize=3"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        assertEquals(3, content.size)
        
        // Second page should have ELEC-005, HOME-001, HOME-002
        assertEquals("ELEC-005", content[0]["sku"])
        assertEquals("HOME-001", content[1]["sku"])
        assertEquals("HOME-002", content[2]["sku"])
        
        val number = response["number"] as Int
        assertEquals(1, number)
    }
    
    @Test
    fun `should combine filtering and sorting`() {
        val result = mockMvc.perform(
            get("/api/products?category=Electronics&sortBy=price&sortDirection=DESC")
        )
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        assertEquals(2, content.size)
        
        // Electronics sorted by price DESC
        assertEquals("ELEC-005", content[0]["sku"])
        assertEquals(150.0, content[0]["price"])
        
        assertEquals("ELEC-001", content[1]["sku"])
        assertEquals(99.99, content[1]["price"])
    }
    
    @Test
    fun `should combine filtering, sorting and pagination`() {
        val result = mockMvc.perform(
            get("/api/products?category=Electronics&sortBy=price&sortDirection=DESC&paginated=true&pageNumber=0&pageSize=2")
        )
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        assertEquals(2, content.size)
        
        // Electronics sorted by price DESC
        assertEquals("ELEC-005", content[0]["sku"])
        assertEquals(150.0, content[0]["price"])
        
        assertEquals("ELEC-001", content[1]["sku"])
        assertEquals(99.99, content[1]["price"])
    }
    
    @Test
    fun `should return bad request for invalid sort field`() {
        val result = mockMvc.perform(get("/api/products?sortBy=invalid"))
            .andExpect(status().isBadRequest)
            .andReturn()
        
        assertEquals(400, result.response.status)
    }
    
    @Test
    fun `should return bad request for invalid sort direction`() {
        val result = mockMvc.perform(get("/api/products?sortDirection=INVALID"))
            .andExpect(status().isBadRequest)
            .andReturn()
        
        assertEquals(400, result.response.status)
    }
    
    @Test
    fun `should handle case-insensitive sort fields`() {
        val result = mockMvc.perform(get("/api/products?sortBy=PRICE&sortDirection=desc"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        
        // Should be sorted by price DESC
        assertEquals("ELEC-005", content[0]["sku"])
        assertEquals(150.0, content[0]["price"])
    }
    
    @Test
    fun `should return products without discount for Clothing category`() {
        val result = mockMvc.perform(get("/api/products?category=Clothing"))
            .andExpect(status().isOk)
            .andReturn()
        
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        
        val content = getContentList(response)
        assertEquals(2, content.size)
        
        // CLOTH-001
        assertEquals("CLOTH-001", content[0]["sku"])
        assertEquals("Clothing", content[0]["category"])
        assertEquals(20.0, content[0]["price"])
        assertEquals(0, content[0]["discount"])
        assertEquals(20.0, content[0]["finalPrice"])
        
        // CLOTH-002
        assertEquals("CLOTH-002", content[1]["sku"])
        assertEquals("Clothing", content[1]["category"])
        assertEquals(35.0, content[1]["price"])
        assertEquals(0, content[1]["discount"])
        assertEquals(35.0, content[1]["finalPrice"])
    }
    
    @Test
    fun `should return bad request for negative page number`() {
        val result = mockMvc.perform(get("/api/products?pageNumber=-1&pageSize=10"))
            .andExpect(status().isBadRequest)
            .andReturn()
        
        assertEquals(400, result.response.status)
    }
    
    @Test
    fun `should return bad request for zero page size`() {
        val result = mockMvc.perform(get("/api/products?pageNumber=0&pageSize=0"))
            .andExpect(status().isBadRequest)
            .andReturn()
        
        assertEquals(400, result.response.status)
    }
    
    @Test
    fun `should return bad request for page size exceeding limit`() {
        val result = mockMvc.perform(get("/api/products?pageNumber=0&pageSize=101"))
            .andExpect(status().isBadRequest)
            .andReturn()
        
        assertEquals(400, result.response.status)
    }

    @Suppress("UNCHECKED_CAST")
    private fun getContentList(response: Map<String, Any>): List<Map<String, Any>> {
        return response["content"] as List<Map<String, Any>>
    }
}
