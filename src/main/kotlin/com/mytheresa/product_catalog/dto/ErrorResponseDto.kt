package com.mytheresa.product_catalog.dto

import java.time.Instant

data class ErrorResponseDto(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val description: String,
    val path: String,
)
