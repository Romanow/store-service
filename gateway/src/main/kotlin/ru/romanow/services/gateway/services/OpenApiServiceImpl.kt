/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.services

import io.swagger.v3.core.util.Yaml
import io.swagger.v3.oas.models.OpenAPI
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

@Service
class OpenApiServiceImpl : OpenApiService {
    override fun read(path: ClassPathResource): OpenAPI {
        val reader = Yaml.mapper().reader()
        return reader.readValue(path.inputStream, OpenAPI::class.java)
    }
}
