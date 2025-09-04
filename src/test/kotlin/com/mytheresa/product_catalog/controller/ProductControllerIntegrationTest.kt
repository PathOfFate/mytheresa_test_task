package com.mytheresa.product_catalog.controller
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
    fun `should return all test products with required fields`() {
        val content = fetchProducts("/api/products")
        assertEquals(7, content.size)
        content.forEach { product ->
            assertNotNull(product["sku"])
            assertNotNull(product["description"])
            assertNotNull(product["category"])
            assertNotNull(product["price"])
            assertNotNull(product["discount"])
            assertNotNull(product["finalPrice"])
        }
        assertEquals("CLOTH-001", content[0]["sku"])
        assertEquals("CLOTH-002", content[1]["sku"])
        assertEquals("ELEC-001", content[2]["sku"])
    }
    @Test
    fun `should filter products by category`() {
        val content = fetchProducts("/api/products?category=Electronics")
        assertEquals(2, content.size)
        content.forEach { product ->
            assertEquals("Electronics", product["category"])
        }
    }
    @Test
    fun `should sort products by SKU ascending`() {
        val content = fetchProducts("/api/products?sortBy=SKU&sortDirection=ASC")
        assertEquals(7, content.size)
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
        val content = fetchProducts("/api/products?sortBy=price&sortDirection=DESC")
        assertEquals(7, content.size)
        assertEquals("ELEC-005", content[0]["sku"])
        assertEquals(150.0, content[0]["price"])
        assertEquals("ELEC-001", content[1]["sku"])
        assertEquals(99.99, content[1]["price"])
        assertEquals("HOME-002", content[2]["sku"])
        assertEquals(45.0, content[2]["price"])
        assertEquals("CLOTH-002", content[3]["sku"])
        assertEquals(35.0, content[3]["price"])
        assertEquals("HOME-001", content[4]["sku"])
        assertEquals(30.0, content[4]["price"])
        assertEquals("OTHER-015", content[5]["sku"])
        assertEquals(25.0, content[5]["price"])
        assertEquals("CLOTH-001", content[6]["sku"])
        assertEquals(20.0, content[6]["price"])
    }
    @Test
    fun `should sort products by category ascending`() {
        val content = fetchProducts("/api/products?sortBy=category&sortDirection=ASC")
        assertEquals("Clothing", content[0]["category"])
        assertEquals("Clothing", content[1]["category"])
        assertEquals("Electronics", content[2]["category"])
        assertEquals("Electronics", content[3]["category"])
        assertEquals("Home & Kitchen", content[4]["category"])
        assertEquals("Home & Kitchen", content[5]["category"])
        assertEquals("Other", content[6]["category"])
    }
    @Test
    fun `should paginate results`() {
        val req1 = mockMvc.perform(get("/api/products?paginated=true&pageNumber=0&pageSize=3"))
            .andExpect(status().isOk)
            .andReturn()
        val response1 = objectMapper.readValue<Map<String, Any>>(req1.response.contentAsString)
        val firstPageContent = extractProductList(response1)
        assertEquals(3, firstPageContent.size)
        assertEquals("CLOTH-001", firstPageContent[0]["sku"])
        assertEquals("CLOTH-002", firstPageContent[1]["sku"])
        assertEquals("ELEC-001", firstPageContent[2]["sku"])
        val totalElements = response1["totalElements"] as Int
        assertEquals(7, totalElements)
        val totalPages = response1["totalPages"] as Int
        assertEquals(3, totalPages)
        val req2 = mockMvc.perform(get("/api/products?paginated=true&pageNumber=1&pageSize=3"))
            .andExpect(status().isOk)
            .andReturn()
        val response2 = objectMapper.readValue<Map<String, Any>>(req2.response.contentAsString)
        val secondPageContent = extractProductList(response2)
        assertEquals(3, secondPageContent.size)
        assertEquals("ELEC-005", secondPageContent[0]["sku"])
        assertEquals("HOME-001", secondPageContent[1]["sku"])
        assertEquals("HOME-002", secondPageContent[2]["sku"])
        val number = response2["number"] as Int
        assertEquals(1, number)
        val thirdPageContent = fetchProducts("/api/products?paginated=true&pageNumber=2&pageSize=3")
        assertEquals(1, thirdPageContent.size)
        assertEquals("OTHER-015", thirdPageContent[0]["sku"])
        assertEquals("Other", thirdPageContent[0]["category"])
    }
    @Test
    fun `should combine filtering and sorting`() {
        val content = fetchProducts("/api/products?category=Electronics&sortBy=price&sortDirection=DESC")
        assertEquals(2, content.size)
        assertEquals("ELEC-005", content[0]["sku"])
        assertEquals(150.0, content[0]["price"])
        assertEquals("ELEC-001", content[1]["sku"])
        assertEquals(99.99, content[1]["price"])
    }
    @Test
    fun `should combine filtering, sorting and pagination`() {
        val content = fetchProducts("/api/products?category=Electronics&sortBy=price&sortDirection=DESC&pageNumber=0&pageSize=2")
        assertEquals(2, content.size)
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
        val result = mockMvc.perform(get("/api/products?sortBy=PrIcE&sortDirection=dEsC"))
            .andExpect(status().isOk)
            .andReturn()
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        val content = extractProductList(response)
        assertEquals("ELEC-005", content[0]["sku"])
        assertEquals(150.0, content[0]["price"])
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
    private fun fetchProducts(endpoint: String): List<Map<String, Any>> {
        val result = mockMvc.perform(get(endpoint))
            .andExpect(status().isOk)
            .andReturn()
        val responseBody = result.response.contentAsString
        val response = objectMapper.readValue<Map<String, Any>>(responseBody)
        val content = extractProductList(response)
        return content
    }
    @Suppress("UNCHECKED_CAST")
    private fun extractProductList(response: Map<String, Any>): List<Map<String, Any>> {
        return response["content"] as List<Map<String, Any>>
    }
}
