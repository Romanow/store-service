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
import ru.romanow.services.gateway.properties.ActuatorSecurityProperties
import ru.romanow.services.gateway.properties.ServerUrlProperties

@Configuration
@EnableConfigurationProperties(value = [ServerUrlProperties::class, ActuatorSecurityProperties::class])
class GatewayConfiguration {

    @Bean
    fun routers(builder: RouteLocatorBuilder, properties: ServerUrlProperties) =
        builder.routes {
            properties.forEach { name, config ->
                route {
                    path(config.pattern)
                    filters {
                        stripPrefix(1)
                    }
                    uri(config.url)
                }
            }
        }
}
