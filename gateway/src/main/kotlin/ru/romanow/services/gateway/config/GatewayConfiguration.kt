/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.romanow.services.gateway.filters.OpenApiRoutePredicate
import ru.romanow.services.gateway.properties.ActuatorSecurityProperties
import ru.romanow.services.gateway.properties.PredicateConfig
import ru.romanow.services.gateway.properties.ServerUrlProperties
import ru.romanow.services.gateway.services.OpenApiService

@Configuration
@EnableConfigurationProperties(value = [ServerUrlProperties::class, ActuatorSecurityProperties::class])
class GatewayConfiguration {

    @Bean
    fun routers(
        builder: RouteLocatorBuilder,
        properties: ServerUrlProperties,
        openApiRoutePredicate: OpenApiRoutePredicate,
        openApiService: OpenApiService
    ) = builder.routes {
        properties.forEach { _, config ->
            val openApi = openApiService.read(config.path)
            route {
                path(config.pattern)
                predicate(
                    openApiRoutePredicate.apply(
                        PredicateConfig(openApi = openApi, prefix = 1, tags = setOf("public"))
                    )
                )
                filters {
                    stripPrefix(1)
                }
                uri(config.url)
            }
        }
    }
}
