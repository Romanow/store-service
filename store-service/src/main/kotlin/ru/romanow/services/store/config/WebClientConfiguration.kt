/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.services.common.properties.ServerUrlProperties

@Configuration
class WebClientConfiguration {

    @Bean
    fun warehouseWebClient(builder: WebClient.Builder, properties: ServerUrlProperties): WebClient =
        builder
            .baseUrl(properties.warehouseUrl!!)
            .filter(ServletBearerExchangeFilterFunction())
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build()

    @Bean
    fun warrantyWebClient(builder: WebClient.Builder, properties: ServerUrlProperties): WebClient =
        builder
            .baseUrl(properties.warrantyUrl!!)
            .filter(ServletBearerExchangeFilterFunction())
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build()
}
