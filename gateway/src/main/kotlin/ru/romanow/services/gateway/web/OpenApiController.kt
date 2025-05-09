/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.web

import io.swagger.v3.oas.models.OpenAPI
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.yaml.snakeyaml.Yaml
import reactor.core.publisher.Mono

@RestController
class OpenApiController {

    @GetMapping("/api/v1/openapi")
    fun openApi(): Mono<String> {
        val openApi = OpenAPI()
        return Mono.just(Yaml().dump(openApi)) // TODO
    }

}
