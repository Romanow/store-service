package ru.romanow.services.common.config

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import reactor.core.publisher.Mono
import ru.romanow.services.common.properties.CircuitBreakerConfigurationProperties

typealias CircuitBreakerFactory =
    ReactiveCircuitBreakerFactory<Resilience4JCircuitBreakerConfiguration, Resilience4JConfigBuilder>

@Configuration
class CircuitBreakerConfiguration {
    private val logger = LoggerFactory.getLogger(CircuitBreakerConfiguration::class.java)

    @Bean
    @ConditionalOnMissingBean(CircuitBreakerConfigurationSupport::class)
    fun circuitBreakerConfigurationSupport(): CircuitBreakerConfigurationSupport {
        return object : CircuitBreakerConfigurationSupport {}
    }

    @Bean
    fun defaultCustomizer(
        circuitBreakerConfigurationSupport: CircuitBreakerConfigurationSupport,
        properties: CircuitBreakerConfigurationProperties
    ): Customizer<ReactiveResilience4JCircuitBreakerFactory> {
        val timeLimiterConfig = TimeLimiterConfig
            .custom()
            .timeoutDuration(properties.defaultTimeout)
            .build()
        val circuitBreakerConfig = CircuitBreakerConfig
            .custom()
            .failureRateThreshold(20f)
            .slowCallRateThreshold(50f)
            .ignoreExceptions(* circuitBreakerConfigurationSupport.ignoredExceptions())
            .build()
        return Customizer {
            it.configureDefault { id ->
                Resilience4JConfigBuilder(id)
                    .timeLimiterConfig(timeLimiterConfig)
                    .circuitBreakerConfig(circuitBreakerConfig)
                    .build()
            }
        }
    }

    @Bean
    fun fallback(circuitBreakerConfigurationSupport: CircuitBreakerConfigurationSupport): Fallback {
        return object : Fallback {
            override fun <T> apply(method: HttpMethod, url: String, throwable: Throwable, vararg params: Any): Mono<T> {
                logger.warn(
                    "Request to {} '{}' failed with exception: {}. (params: '{}')",
                    method.name(), url, throwable.message, params
                )
                if (throwable.javaClass in circuitBreakerConfigurationSupport.ignoredExceptions()) {
                    throw (throwable as RuntimeException)
                }
                return Mono.empty()
            }
        }
    }
}
