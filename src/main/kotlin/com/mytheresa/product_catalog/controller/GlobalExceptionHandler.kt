package com.mytheresa.product_catalog.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, Any?>> {
        val response = mapOf<String, Any?>(
            "error" to "Bad Request",
            "message" to ex.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }
}
