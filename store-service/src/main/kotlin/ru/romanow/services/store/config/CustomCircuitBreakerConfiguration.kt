/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.config

import jakarta.persistence.EntityNotFoundException
import org.springframework.context.annotation.Configuration
import ru.romanow.services.common.config.CircuitBreakerConfigurationSupport
import ru.romanow.services.store.exceptions.ItemNotAvailableException
import ru.romanow.services.store.exceptions.ItemNotOnWarrantyException

@Configuration
class CustomCircuitBreakerConfiguration : CircuitBreakerConfigurationSupport {
    override fun ignoredExceptions(): Array<Class<out Throwable>> {
        return arrayOf(
            EntityNotFoundException::class.java,
            ItemNotAvailableException::class.java,
            ItemNotOnWarrantyException::class.java
        )
    }
}
