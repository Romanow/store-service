/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.*
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.services.common.config.CircuitBreakerFactory
import ru.romanow.services.common.config.FallbackHandler
import ru.romanow.services.common.properties.CircuitBreakerProperties
import ru.romanow.services.common.properties.ServerUrlProperties
import ru.romanow.services.common.utils.buildEx
import ru.romanow.services.store.exceptions.ItemNotAvailableException
import ru.romanow.services.store.exceptions.WarehouseProcessException
import ru.romanow.services.store.service.WarehouseClient
import ru.romanow.services.warehouse.models.ItemInfo
import java.util.*

@Service
internal class WarehouseClientImpl(
    private val fallback: FallbackHandler,
    private val warehouseWebClient: WebClient,
    private val serverUrlProperties: ServerUrlProperties,
    private val circuitBreakerProperties: CircuitBreakerProperties,
    private val circuitBreakerFactory: CircuitBreakerFactory
) : WarehouseClient {

    override fun items(names: List<String>): Optional<List<ItemInfo>> {
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
                    circuitBreakerFactory
                        .create("Items Details")
                        .run(it) { throwable ->
                            fallback.apply(
                                method = GET,
                                url = "${serverUrlProperties.warehouseUrl}/api/protected/v1/items",
                                throwable = throwable,
                                params = arrayOf(names)
                            )
                        }
                } else {
                    return@transform it
                }
            }
            .blockOptional()
    }

    override fun take(items: List<String>) {
        warehouseWebClient
            .post()
            .uri("/api/protected/v1/items/take")
            .body(BodyInserters.fromValue(items))
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it == CONFLICT }, { response -> buildEx(response) { ItemNotAvailableException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { WarehouseProcessException(it) } })
            .toBodilessEntity()
            .transform {
                if (circuitBreakerProperties.enabled) {
                    circuitBreakerFactory
                        .create("Take Items")
                        .run(it) { throwable ->
                            fallback.apply(
                                method = POST,
                                url = "${serverUrlProperties.warehouseUrl}/api/protected/v1/items/take",
                                throwable = throwable,
                                useFallback = false,
                                params = arrayOf(items)
                            )
                        }
                } else {
                    return@transform it
                }
            }
            .blockOptional()
    }

    override fun refund(items: List<String>) {
        warehouseWebClient
            .method(DELETE)
            .uri("/api/protected/v1/items/refund")
            .body(BodyInserters.fromValue(items))
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { WarehouseProcessException(it) } })
            .toBodilessEntity()
            .transform {
                if (circuitBreakerProperties.enabled) {
                    circuitBreakerFactory
                        .create("Refund Items")
                        .run(it) { throwable ->
                            fallback.apply(
                                method = DELETE,
                                url = "${serverUrlProperties.warehouseUrl}/api/protected/v1/items/refund",
                                throwable = throwable,
                                params = arrayOf(items)
                            )
                        }
                } else {
                    return@transform it
                }
            }
            .blockOptional()
    }
}
