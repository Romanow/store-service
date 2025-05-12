/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "circuit-breaker")
data class CircuitBreakerProperties(
    val enabled: Boolean,
)
