/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.services.common.config.CircuitBreakerFactory
import ru.romanow.services.common.config.Fallback
import ru.romanow.services.common.properties.CircuitBreakerConfigurationProperties
import ru.romanow.services.common.properties.ServerUrlProperties
import ru.romanow.services.store.service.WarehouseClient
import java.util.*

@Service
class WarehouseClientImpl(
    private val fallback: Fallback,
    private val warehouseWebClient: WebClient,
    private val serverUrlProperties: ServerUrlProperties,
    private val circuitBreakerProperties: CircuitBreakerConfigurationProperties,
    private val factory: CircuitBreakerFactory
) : WarehouseClient {

    override fun items(names: List<String>): Optional<List<*>> {
        TODO("Not yet implemented")
    }
}
