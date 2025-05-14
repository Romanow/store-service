/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.common.config

import org.springframework.http.HttpMethod
import reactor.core.publisher.Mono

@FunctionalInterface
interface FallbackHandler {
    fun <T> apply(
        method: HttpMethod, url: String, throwable: Throwable, useFallback: Boolean = true, vararg params: Any
    ): Mono<T>
}
