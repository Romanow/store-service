/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.common.config

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.TIME_BASED
import org.slf4j.LoggerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import reactor.core.publisher.Mono
import ru.romanow.services.common.exceptions.CircuitBreakerIgnoredException
import ru.romanow.services.common.exceptions.CircuitBreakerThrowableException
import ru.romanow.services.common.properties.CircuitBreakerProperties

typealias CircuitBreakerFactory =
    ReactiveCircuitBreakerFactory<Resilience4JCircuitBreakerConfiguration, Resilience4JConfigBuilder>

@Configuration
class CircuitBreakerConfiguration {
    private val logger = LoggerFactory.getLogger(CircuitBreakerConfiguration::class.java)

    @Bean
    fun defaultCustomizer(properties: CircuitBreakerProperties): Customizer<ReactiveResilience4JCircuitBreakerFactory> {
        val circuitBreakerConfig = CircuitBreakerConfig
            .custom()
            .failureRateThreshold(20f)
            .slidingWindowSize(100)
            .slidingWindowType(TIME_BASED)
            .minimumNumberOfCalls(10)
            .slowCallRateThreshold(50f)
            .ignoreException { it is CircuitBreakerIgnoredException }
            .build()
        return Customizer {
            it.configureDefault { id ->
                Resilience4JConfigBuilder(id).circuitBreakerConfig(circuitBreakerConfig).build()
            }
        }
    }

    @Bean
    fun fallback(): Fallback {
        return object : Fallback {
            override fun <T> apply(method: HttpMethod, url: String, throwable: Throwable, vararg params: Any): Mono<T> {
                logger.warn(
                    "Request to {} '{}' failed with exception: {}. (params: '{}')",
                    method.name(), url, throwable.message, params
                )
                if (throwable is CircuitBreakerIgnoredException || throwable is CircuitBreakerThrowableException) {
                    throw throwable
                }
                return Mono.empty()
            }
        }
    }
}
