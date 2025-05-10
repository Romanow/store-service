package ru.romanow.services.gateway.web

import io.swagger.v3.core.util.Yaml
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.romanow.openapi.aggregator.OpenApiAggregatorService
import ru.romanow.services.gateway.properties.ServerUrlProperties

@RestController
@RequestMapping("/api/v1/openapi")
class OpenApiController(
    private val openApiAggregatorService: OpenApiAggregatorService,
    serverUrlProperties: ServerUrlProperties
) {
    private val declarations =
        serverUrlProperties.map { it.value.pattern.substringBefore("/**") to it.value.path.path }

    @GetMapping
    fun openApi(): Mono<String> {
        val openApi = openApiAggregatorService
            .aggregateOpenApi(declarations, setOf("public"), setOf(), info, servers)
        return Mono.just(Yaml.pretty().writeValueAsString(openApi))
    }

    private val info = Info()
        .also {
            it.title = "Store Service"
            it.version = "1.0.0"
        }

    private val servers = listOf(
        Server().also {
            it.url = "http://localhost:8080"
            it.description = "Local server"
        }
    )
}
