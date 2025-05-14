/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod.*
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.services.common.config.CircuitBreakerFactory
import ru.romanow.services.common.config.FallbackHandler
import ru.romanow.services.common.properties.CircuitBreakerProperties
import ru.romanow.services.common.properties.ServerUrlProperties
import ru.romanow.services.common.utils.buildEx
import ru.romanow.services.store.exceptions.ItemNotOnWarrantyException
import ru.romanow.services.store.exceptions.WarrantyProcessException
import ru.romanow.services.store.service.WarrantyClient
import ru.romanow.services.warranty.models.WarrantyResponse
import ru.romanow.services.warranty.models.WarrantyStatusResponse
import java.util.*

@Service
internal class WarrantyClientImpl(
    private val fallback: FallbackHandler,
    private val warrantyWebClient: WebClient,
    private val serverUrlProperties: ServerUrlProperties,
    private val circuitBreakerProperties: CircuitBreakerProperties,
    private val circuitBreakerFactory: CircuitBreakerFactory
) : WarrantyClient {

    override fun status(orderUid: UUID): Optional<List<WarrantyStatusResponse>> {
        val type = object : ParameterizedTypeReference<List<WarrantyStatusResponse>>() {}
        return warrantyWebClient
            .get()
            .uri("/api/protected/v1/warranty/$orderUid")
            .retrieve()
            .onStatus({ it.isError }, { response -> buildEx(response) { WarrantyProcessException(it) } })
            .bodyToMono(type)
            .transform {
                if (circuitBreakerProperties.enabled) {
                    circuitBreakerFactory
                        .create("Warranty Status")
                        .run(it) { throwable ->
                            fallback.apply(
                                method = GET,
                                url = "${serverUrlProperties.warrantyUrl}/api/protected/v1/warranty/$orderUid",
                                throwable = throwable
                            )
                        }
                } else {
                    return@transform it
                }
            }
            .blockOptional()
    }

    override fun request(orderUid: UUID, items: List<String>): Optional<List<WarrantyResponse>> {
        val type = object : ParameterizedTypeReference<List<WarrantyResponse>>() {}
        return warrantyWebClient
            .post()
            .uri("/api/protected/v1/warranty/$orderUid/request")
            .body(BodyInserters.fromValue(items))
            .retrieve()
            .onStatus({ it == CONFLICT }, { response -> buildEx(response) { ItemNotOnWarrantyException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { WarrantyProcessException(it) } })
            .bodyToMono(type)
            .transform {
                if (circuitBreakerProperties.enabled) {
                    circuitBreakerFactory
                        .create("Warranty Request")
                        .run(it) { throwable ->
                            fallback.apply(
                                method = POST,
                                url = "${serverUrlProperties.warrantyUrl}/api/protected/v1/warranty/$orderUid/request",
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

    override fun start(orderUid: UUID, items: List<String>) {
        warrantyWebClient
            .post()
            .uri("/api/protected/v1/warranty/$orderUid/start")
            .body(BodyInserters.fromValue(items))
            .retrieve()
            .onStatus({ it.isError }, { response -> buildEx(response) { WarrantyProcessException(it) } })
            .toBodilessEntity()
            .transform {
                if (circuitBreakerProperties.enabled) {
                    circuitBreakerFactory
                        .create("Start Warranty")
                        .run(it) { throwable ->
                            fallback.apply(
                                method = POST,
                                url = "${serverUrlProperties.warrantyUrl}/api/protected/v1/warranty/$orderUid/start",
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

    override fun stop(orderUid: UUID) {
        warrantyWebClient
            .method(DELETE)
            .uri("/api/protected/v1/warranty/$orderUid/stop")
            .retrieve()
            .onStatus({ it.isError }, { response -> buildEx(response) { WarrantyProcessException(it) } })
            .toBodilessEntity()
            .transform {
                if (circuitBreakerProperties.enabled) {
                    circuitBreakerFactory
                        .create("Stop Warranty")
                        .run(it) { throwable ->
                            fallback.apply(
                                method = DELETE,
                                url = "${serverUrlProperties.warehouseUrl}/api/protected/v1/warranty/$orderUid/stop",
                                throwable = throwable,
                                useFallback = false
                            )
                        }
                } else {
                    return@transform it
                }
            }
            .blockOptional()
    }
}
