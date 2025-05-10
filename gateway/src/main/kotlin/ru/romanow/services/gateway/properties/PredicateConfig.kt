/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.properties

import io.swagger.v3.oas.models.OpenAPI

data class PredicateConfig(
    val openApi: OpenAPI,
    val prefix: Int,
    val tags: Set<String>? = null
)
