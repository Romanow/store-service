/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.POST
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.services.common.config.CircuitBreakerFactory
import ru.romanow.services.common.config.Fallback
import ru.romanow.services.common.properties.CircuitBreakerConfigurationProperties
import ru.romanow.services.common.properties.ServerUrlProperties
import ru.romanow.services.common.utils.buildEx
import ru.romanow.services.store.exceptions.WarehouseProcessException
import ru.romanow.services.store.service.WarrantyClient
import ru.romanow.services.warranty.model.WarrantyStatusResponse
import java.util.*

@Service
class WarrantyClientImpl(
    private val fallback: Fallback,
    private val warrantyWebClient: WebClient,
    private val properties: ServerUrlProperties,
    private val circuitBreakerProperties: CircuitBreakerConfigurationProperties,
    private val factory: CircuitBreakerFactory
) : WarrantyClient {
    override fun warrantyStatus(orderUid: UUID): Optional<List<WarrantyStatusResponse>> =
        warrantyWebClient
            .get()
            .uri("/api/private/v1/warranty/$orderUid")
            .retrieve()
            .onStatus({ it.isError }, { response -> buildEx(response) { WarehouseProcessException(it) } })
            .bodyToMono(object : ParameterizedTypeReference<List<WarrantyStatusResponse>>() {})
            .transform {
                if (circuitBreakerProperties.enabled) {
                    factory.create("warrantyStatus").run(it) { throwable ->
                        fallback.apply(POST, "${properties.warehouseUrl}/api/private/v1/warranty/$orderUid", throwable)
                    }
                } else {
                    return@transform it
                }
            }
            .blockOptional()
}
