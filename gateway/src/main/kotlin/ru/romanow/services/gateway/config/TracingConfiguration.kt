/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.config

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationPredicate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext
import org.springframework.security.config.observation.SecurityObservationSettings

@Configuration
@ConditionalOnProperty("management.tracing.enabled", havingValue = "true")
class TracingConfiguration {

    @Bean
    fun noActuatorObservations() = ObservationPredicate { name: String, context: Observation.Context? ->
        if (name == "http.server.requests" && context is ServerRequestObservationContext) {
            !context.carrier.path.pathWithinApplication().value().startsWith("/manage")
        } else {
            true
        }
    }

    @Bean
    fun noSpringSecurityObservations(): SecurityObservationSettings {
        return SecurityObservationSettings.noObservations()
    }
}
