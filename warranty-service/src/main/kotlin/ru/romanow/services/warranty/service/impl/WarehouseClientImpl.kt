/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.services.common.config.CircuitBreakerFactory
import ru.romanow.services.common.config.Fallback
import ru.romanow.services.common.properties.CircuitBreakerProperties
import ru.romanow.services.common.properties.ServerUrlProperties
import ru.romanow.services.common.utils.buildEx
import ru.romanow.services.warehouse.models.ItemInfo
import ru.romanow.services.warranty.exceptions.WarehouseProcessException
import ru.romanow.services.warranty.service.WarehouseClient
import java.util.*

@Service
internal class WarehouseClientImpl(
    private val fallback: Fallback,
    private val warehouseWebClient: WebClient,
    private val serverUrlProperties: ServerUrlProperties,
    private val circuitBreakerProperties: CircuitBreakerProperties,
    private val factory: CircuitBreakerFactory
) : WarehouseClient {

    override fun items(names: Set<String>): Optional<List<ItemInfo>> {
        val type = object : ParameterizedTypeReference<List<ItemInfo>>() {}
        return warehouseWebClient
            .get()
            .uri { it.path("/api/protected/v1/items").queryParam("names", names).build() }
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { WarehouseProcessException(it) } })
            .bodyToMono(type)
            .transform {
                if (circuitBreakerProperties.enabled) {
                    factory.create("Items Details").run(it) { throwable ->
                        fallback.apply(GET, "${serverUrlProperties.warehouseUrl}/api/protected/v1/items", throwable)
                    }
                } else {
                    return@transform it
                }
            }
            .blockOptional()
    }
}
