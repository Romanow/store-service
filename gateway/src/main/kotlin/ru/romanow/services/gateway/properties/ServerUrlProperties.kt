/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.ClassPathResource
import java.net.URI

@ConfigurationProperties(prefix = "services")
class ServerUrlProperties : HashMap<String, RouteConfig>()

data class RouteConfig(
    val url: URI,
    val pattern: String,
    val path: ClassPathResource
)
