/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.config

import org.springframework.context.annotation.Configuration
import ru.romanow.services.common.config.CircuitBreakerConfigurationSupport
import ru.romanow.services.store.exceptions.WarehouseProcessException
import ru.romanow.services.store.exceptions.WarrantyProcessException

@Configuration
class CustomCircuitBreakerConfiguration : CircuitBreakerConfigurationSupport {
    override fun ignoredExceptions(): Array<Class<out Throwable>> {
        return arrayOf(
            WarehouseProcessException::class.java,
            WarrantyProcessException::class.java
        )
    }
}
