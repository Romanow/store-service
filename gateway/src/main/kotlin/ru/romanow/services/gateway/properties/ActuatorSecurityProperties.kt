/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "management.credentials")
data class ActuatorSecurityProperties(
    val user: String,
    val passwd: String,
    val role: String
)
