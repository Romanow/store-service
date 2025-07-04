/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.filters

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
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
        return@Predicate findOperations(config.openApi, method, path, config.tags).isNotEmpty()
    }

    fun findOperations(
        openApi: OpenAPI,
        requestMethod: HttpMethod,
        requestPath: PathContainer,
        tags: Set<String>?
    ): List<Operation> {
        val matcher = AntPathMatcher()
        val method = PathItem.HttpMethod.valueOf(requestMethod.name())
        val result = mutableListOf<Operation>()
        for ((path, pathItem) in openApi.paths) {
            val operation: Operation? = pathItem.readOperationsMap()[method]
            if (operation != null && matcher.match(path, requestPath.value()) &&
                (tags.isNullOrEmpty() || operation.tags.containsAll(tags))
            ) {
                val hasPathParameters = operation.parameters == null || operation.parameters.none { it.`in` == "path" }
                if (!hasPathParameters) {
                    val params = matcher.extractUriTemplateVariables(path, requestPath.value())
                    val exists = operation.parameters
                        .filter { it.name in params }
                        .any {
                            val value = params[it.name]!!
                            return@any checkType(value, it.schema.type) &&
                                checkSubtype(value, it.schema.format) &&
                                (it.schema.pattern == null || it.schema.pattern.toRegex().matches(value))
                        }
                    if (exists) {
                        result.add(operation)
                    }
                } else {
                    result.add(operation)
                }
            }
        }
        return result
    }

    private fun checkType(value: String, type: String?) =
        when (type) {
            "number", "integer" -> value.all { it.isDigit() }
            "boolean" -> value.toBooleanStrictOrNull() != null
            "string" -> true
            else -> true
        }

    private fun checkSubtype(value: String, type: String) =
        when (type) {
            "int32", "int64" -> value.all { it.isDigit() }
            "uuid" -> UUID_REGEX.matches(value)
            else -> true
        }

    private fun trimSlash(path: RequestPath, trimPrefix: Int): PathContainer =
        if (path.value().endsWith("/")) {
            path.subPath(2 * trimPrefix, path.elements().size - 1)
        } else {
            path.subPath(2 * trimPrefix)
        }

    companion object {
        private val UUID_REGEX = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}".toRegex()
    }
}
