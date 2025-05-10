/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class OpenApiController {

    @GetMapping("/api/v1/openapi")
    fun openApi(): Mono<String> {
//        val aggregatorService = DefaultOpenApiAggregatorService()
//        val openApi = aggregatorService.aggregateOpenApi()
        return Mono.empty() // Mono.just(Yaml.pretty().writeValueAsString(openApi))
    }

}
