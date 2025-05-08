/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.GET
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.services.common.config.CircuitBreakerFactory
import ru.romanow.services.common.config.Fallback
import ru.romanow.services.common.properties.CircuitBreakerConfigurationProperties
import ru.romanow.services.common.properties.ServerUrlProperties
import ru.romanow.services.common.utils.buildEx
import ru.romanow.services.store.exceptions.WarrantyProcessException
import ru.romanow.services.store.service.WarrantyClient
import ru.romanow.services.warranty.models.WarrantyStatusResponse
import java.util.*

@Service
internal class WarrantyClientImpl(
    private val fallback: Fallback,
    private val warrantyWebClient: WebClient,
    private val serverUrlProperties: ServerUrlProperties,
    private val circuitBreakerProperties: CircuitBreakerConfigurationProperties,
    private val factory: CircuitBreakerFactory
) : WarrantyClient {

    override fun status(orderUid: UUID): Optional<List<WarrantyStatusResponse>> {
        val type = object : ParameterizedTypeReference<List<WarrantyStatusResponse>>() {}
        return warrantyWebClient
            .get()
            .uri("/api/private/v1/warranty/$orderUid")
            .retrieve()
            .onStatus({ it.isError }, { response -> buildEx(response) { WarrantyProcessException(it) } })
            .bodyToMono(type)
            .transform {
                if (circuitBreakerProperties.enabled) {
                    factory.create("items").run(it) { throwable ->
                        fallback.apply(GET, "${serverUrlProperties.warehouseUrl}/api/private/v1/warranty/$orderUid", throwable)
                    }
                } else {
                    return@transform it
                }
            }
            .blockOptional()
    }
}
