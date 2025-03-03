/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.common.utils

import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import ru.romanow.services.common.model.ErrorResponse
import java.util.function.Function

fun <T : RuntimeException> buildEx(response: ClientResponse, func: Function<String?, T>): Mono<T> =
    response.bodyToMono(ErrorResponse::class.java).flatMap { b -> Mono.error { func.apply(b.message) } }
