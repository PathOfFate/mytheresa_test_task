package com.mytheresa.product_catalog.repository

import com.mytheresa.product_catalog.entity.ProductEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<ProductEntity, String> {

    fun findByCategoryIgnoreCase(category: String, pageable: Pageable): Page<ProductEntity>
}
