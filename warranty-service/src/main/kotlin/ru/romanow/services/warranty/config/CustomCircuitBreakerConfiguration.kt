/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.config

import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Configuration
import ru.romanow.services.common.config.CircuitBreakerConfigurationSupport

@Configuration
class CustomCircuitBreakerConfiguration : CircuitBreakerConfigurationSupport {
    override fun ignoredExceptions(): Array<Class<out Throwable>> {
        return arrayOf(EntityNotFoundException::class.java)
    }
}
