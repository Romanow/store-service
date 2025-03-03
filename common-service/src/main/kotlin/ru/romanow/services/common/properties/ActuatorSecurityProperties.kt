/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.common.properties

import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "management.credentials")
data class ActuatorSecurityProperties(
    @field:NotEmpty val user: String,
    @field:NotEmpty val passwd: String,
    @field:NotEmpty val role: String
)
