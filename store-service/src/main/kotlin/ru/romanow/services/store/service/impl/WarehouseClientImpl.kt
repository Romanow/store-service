/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.services.common.config.CircuitBreakerFactory
import ru.romanow.services.common.config.Fallback
import ru.romanow.services.common.properties.CircuitBreakerConfigurationProperties
import ru.romanow.services.common.properties.ServerUrlProperties
import ru.romanow.services.common.utils.buildEx
import ru.romanow.services.store.exceptions.WarehouseProcessException
import ru.romanow.services.store.service.WarehouseClient
import ru.romanow.services.warehouse.model.ItemResponse
import java.util.*

@Service
class WarehouseClientImpl(
    private val fallback: Fallback,
    private val warehouseWebClient: WebClient,
    private val serverUrlProperties: ServerUrlProperties,
    private val circuitBreakerProperties: CircuitBreakerConfigurationProperties,
    private val factory: CircuitBreakerFactory
) : WarehouseClient {

    override fun items(names: List<String>): Optional<List<ItemResponse>> {
        return warehouseWebClient
            .put()
            .uri("/api/private/v1/items")
            .body(BodyInserters.fromValue(names))
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { WarehouseProcessException(it) } })
            .bodyToMono(object : ParameterizedTypeReference<List<ItemResponse>>() {})
            .transform {
                if (circuitBreakerProperties.enabled) {
                    factory.create("items").run(it) { throwable ->
                        fallback.apply(POST, "${serverUrlProperties.warehouseUrl}/api/private/v1/items", throwable)
                    }
                } else {
                    return@transform it
                }
            }
            .blockOptional()
    }
}
