package ru.romanow.services.gateway.filters

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.server.PathContainer
import ru.romanow.services.gateway.services.OpenApiServiceImpl
import java.util.*
import java.util.stream.Stream

class OpenApiRoutePredicateTest {

    private val openApiService = OpenApiServiceImpl()
    private val openApiRoutePredicate = OpenApiRoutePredicate()

    @ParameterizedTest
    @ArgumentsSource(ValueProvider::class)
    fun testCheckIsOperationExists(path: String, method: HttpMethod, expectedCount: Int) {
        val openApi = openApiService.read(OPEN_API)

        val result = openApiRoutePredicate
            .findOperations(openApi, method, PathContainer.parsePath(path), setOf("public"))

        assertThat(result).hasSize(expectedCount)
    }

    internal class ValueProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> =
            Stream.of(
                of("/api/public/v1/echo", GET, 1),
                of("/api/public/v1/${UUID.randomUUID()}", GET, 1),
                of("/api/public/v1/100", GET, 1),
                of("/api/public/v1/test", GET, 0),
                of("/api/public/v1/echo", POST, 0),
                of("/api/protected/v1/echo", GET, 0)
            )
    }

    companion object {
        private val OPEN_API = ClassPathResource("openapi/test.yml")
    }
}
