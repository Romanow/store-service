/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.filters

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory
import org.springframework.http.HttpMethod
import org.springframework.http.server.PathContainer
import org.springframework.http.server.RequestPath
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import ru.romanow.services.gateway.properties.PredicateConfig
import java.util.function.Predicate

@Component
class OpenApiRoutePredicate : AbstractRoutePredicateFactory<PredicateConfig>(PredicateConfig::class.java) {
    override fun apply(config: PredicateConfig) = Predicate<ServerWebExchange> {
        val method = it.request.method
        val path = trimSlash(it.request.path, config.prefix)
        return@Predicate checkIsOperationExists(config.openAPI, method, path)
    }

    fun checkIsOperationExists(openApi: OpenAPI, method: HttpMethod, path: PathContainer) =
        openApi.paths.entries.stream().filter { isOperationMatch(it, method, path) }.count() > 0

    private fun isOperationMatch(
        path: Map.Entry<String, PathItem>, requestMethod: HttpMethod, requestPath: PathContainer
    ): Boolean {
        val method = PathItem.HttpMethod.valueOf(requestMethod.name())
        val operation = path.value.readOperationsMap()[method]
        val matcher = AntPathMatcher()
        return operation != null
            && matcher.match(path.key, requestPath.value())
            && operation.tags.contains(PUBLIC_TAG)
    }

    private fun trimSlash(path: RequestPath, trimPrefix: Int): PathContainer =
        if (path.value().endsWith("/")) {
            path.subPath(2 * trimPrefix, path.elements().size - 1)
        } else {
            path.subPath(2 * trimPrefix)
        }

    companion object {
        private const val PUBLIC_TAG = "public"
    }
}
