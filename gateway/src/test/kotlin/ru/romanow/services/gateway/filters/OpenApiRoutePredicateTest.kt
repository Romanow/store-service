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
import java.util.stream.Stream

class OpenApiRoutePredicateTest {

    private val openApiService = OpenApiServiceImpl()
    private val openApiRoutePredicate = OpenApiRoutePredicate()

    @ParameterizedTest
    @ArgumentsSource(ValueProvider::class)
    fun testCheckIsOperationExists(path: String, method: HttpMethod, expected: Boolean) {
        val openApi = openApiService.read(OPEN_API)

        val result = openApiRoutePredicate
            .checkIsOperationExists(openApi, method, PathContainer.parsePath(path), setOf("public"))

        assertThat(result).isEqualTo(expected)
    }

    internal class ValueProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> =
            Stream.of(
                of("/api/public/v1/echo", GET, true),
                of("/api/public/v1/echo", POST, false),
                of("/api/protected/v1/echo", GET, false)
            )
    }

    companion object {
        private val OPEN_API = ClassPathResource("openapi/test.yml")
    }
}
