package ru.romanow.services.common.config

import org.springframework.http.HttpMethod
import reactor.core.publisher.Mono

@FunctionalInterface
interface Fallback {
    fun <T> apply(method: HttpMethod, url: String, throwable: Throwable, vararg params: Any): Mono<T>
}
