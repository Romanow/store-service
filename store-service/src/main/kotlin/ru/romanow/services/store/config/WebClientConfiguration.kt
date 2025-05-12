/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.config

import io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import ru.romanow.services.common.properties.ServerUrlProperties
import java.time.Duration

@Configuration
class WebClientConfiguration {

    @Bean
    fun warehouseWebClient(builder: WebClient.Builder, properties: ServerUrlProperties): WebClient =
        builder
            .baseUrl(properties.warehouseUrl!!)
            .clientConnector(ReactorClientHttpConnector(httpClient()))
            .filter(ServletBearerExchangeFilterFunction())
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build()

    @Bean
    fun warrantyWebClient(builder: WebClient.Builder, properties: ServerUrlProperties): WebClient =
        builder
            .baseUrl(properties.warrantyUrl!!)
            .clientConnector(ReactorClientHttpConnector(httpClient()))
            .filter(ServletBearerExchangeFilterFunction())
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .build()

    private fun httpClient() = HttpClient.create()
        .option(CONNECT_TIMEOUT_MILLIS, 100)
        .responseTimeout(Duration.ofSeconds(1))
}
