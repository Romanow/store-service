/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.services

import io.swagger.v3.oas.models.OpenAPI
import org.springframework.core.io.ClassPathResource

interface OpenApiService {
    fun read(path: ClassPathResource): OpenAPI
}
