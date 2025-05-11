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
import ru.romanow.services.common.config.Fallback
import ru.romanow.services.common.properties.CircuitBreakerConfigurationProperties
import ru.romanow.services.common.properties.ServerUrlProperties
import ru.romanow.services.common.utils.buildEx
import ru.romanow.services.store.exceptions.WarrantyProcessException
import ru.romanow.services.store.service.WarrantyClient
import ru.romanow.services.warranty.models.WarrantyResponse
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
            .uri("/api/protected/v1/warranty/$orderUid")
            .retrieve()
            .onStatus({ it.isError }, { response -> buildEx(response) { WarrantyProcessException(it) } })
            .bodyToMono(type)
            .transform {
                if (circuitBreakerProperties.enabled) {
                    factory.create("status").run(it) { throwable ->
                        fallback.apply(
                            GET, "${serverUrlProperties.warehouseUrl}/api/protected/v1/warranty/$orderUid",
                            throwable
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
            .onStatus({ it == CONFLICT }, { response -> buildEx(response) { WarrantyProcessException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { WarrantyProcessException(it) } })
            .bodyToMono(type)
            .transform {
                if (circuitBreakerProperties.enabled) {
                    factory.create("request").run(it) { throwable ->
                        fallback.apply(
                            GET, "${serverUrlProperties.warehouseUrl}/api/protected/v1/warranty/$orderUid/request",
                            throwable
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
                    factory.create("start").run(it) { throwable ->
                        fallback.apply(
                            POST, "${serverUrlProperties.warehouseUrl}/api/protected/v1/warranty/$orderUid/start",
                            throwable
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
                    factory.create("stop").run(it) { throwable ->
                        fallback.apply(
                            DELETE, "${serverUrlProperties.warehouseUrl}/api/protected/v1/warranty/$orderUid/stop",
                            throwable
                        )
                    }
                } else {
                    return@transform it
                }
            }
            .blockOptional()
    }
}
