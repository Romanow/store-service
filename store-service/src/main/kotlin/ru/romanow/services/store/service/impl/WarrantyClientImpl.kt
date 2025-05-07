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
import ru.romanow.services.store.service.WarrantyClient
import java.util.*

@Service
internal class WarrantyClientImpl(
    private val fallback: Fallback,
    private val warrantyWebClient: WebClient,
    private val properties: ServerUrlProperties,
    private val circuitBreakerProperties: CircuitBreakerConfigurationProperties,
    private val factory: CircuitBreakerFactory
) : WarrantyClient {
    override fun warrantyStatus(orderUid: UUID): Optional<List<*>> {
        TODO("Not yet implemented")
    }
}
