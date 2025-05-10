package ru.romanow.services.gateway

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.hamcrest.Matchers.emptyOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.TEXT_PLAIN_VALUE
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.wiremock.spring.EnableWireMock
import ru.romanow.services.gateway.config.KeycloakConfiguration
import java.net.URI

@ActiveProfiles("test")
@SpringBootTest(
    properties = [
        "services.test.url=http://localhost:\${wiremock.server.port}",
        "services.test.pattern=/test/**",
        "services.test.path=openapi/test.yml",
    ]
)
@EnableWireMock
@AutoConfigureWebTestClient
@AutoConfigureObservability
@Import(KeycloakConfiguration::class)
internal class GatewayApplicationTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var builder: WebClient.Builder

    @Value("\${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private lateinit var issuerUri: URI

    @Value("\${spring.security.oauth2.client.registration.keycloak.client-id}")
    private lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private lateinit var clientSecret: String

    @ParameterizedTest
    @ValueSource(strings = ["health", "prometheus"])
    fun `when call public manage endpoints then success`(path: String) {
        webClient.get()
            .uri("/manage/$path")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String::class.java).value(not(emptyOrNullString()))
    }

    @Test
    fun `when call secured manage endpoints then unauthorized`() {
        webClient.get()
            .uri("/manage/info")
            .exchange()
            .expectStatus().isUnauthorized()
    }

    @Test
    fun `when call secured manage endpoints with auth then unauthorized`() {
        webClient.get()
            .uri("/manage/info")
            .headers { it.setBasicAuth("management", "passwd") }
            .exchange()
            .expectStatus().isOk()
            .expectBody(String::class.java).value(not(emptyOrNullString()))
    }

    @Test
    fun `when call public api then unauthorized`() {
        webClient.get()
            .uri("/test/api/v1/echo?message=hello")
            .exchange()
            .expectStatus().isUnauthorized()
    }

    @Test
    fun `when call public api with auth then success`() {
        val greeting = "hello"
        stubFor(
            get(urlPathEqualTo("/api/public/v1/echo"))
                .withQueryParam("message", havingExactly(greeting))
                .willReturn(ok().withHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE).withBody(greeting))
        )

        webClient.get()
            .uri("/test/api/public/v1/echo?message=$greeting")
            .header(AUTHORIZATION, "Bearer ${accessToken()}")
            .exchange()
            .expectStatus().isOk()
            .expectBody().consumeWith { equalTo("hello") }
    }

    @Test
    fun `when call missing api with auth then notFound`() {
        val greeting = "hello"
        stubFor(
            get(urlPathEqualTo("/api/public/v1/missing"))
                .willReturn(ok().withHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE).withBody(greeting))
        )
        webClient.get()
            .uri("/test/api/public/v1/missing")
            .header(AUTHORIZATION, "Bearer ${accessToken()}")
            .exchange()
            .expectStatus().isNotFound()
    }

    @Test
    fun `when call private api with auth then notFound`() {
        val greeting = "hello"
        stubFor(
            get(urlPathEqualTo("/api/public/v1/echo"))
                .withQueryParam("message", havingExactly(greeting))
                .willReturn(ok().withHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE).withBody(greeting))
        )
        webClient.get()
            .uri("/test/api/private/v1/echo")
            .header(AUTHORIZATION, "Bearer ${accessToken()}")
            .exchange()
            .expectStatus().isNotFound()
    }

    private fun accessToken(): String {
        val type = object : ParameterizedTypeReference<Map<String, String>>() {}
        return builder.build()
            .post()
            .uri("http://${issuerUri.authority}/realms/master/protocol/openid-connect/token")
            .body(
                BodyInserters.fromFormData("client_id", clientId)
                    .with("client_secret", clientSecret)
                    .with("username", LOGIN)
                    .with("password", PASSWORD)
                    .with("grant_type", "password")
            )
            .retrieve()
            .bodyToMono(type)
            .block()
            ?.get("access_token") as String
    }

    companion object {
        private const val LOGIN = "admin"
        private const val PASSWORD = "admin"
    }
}
