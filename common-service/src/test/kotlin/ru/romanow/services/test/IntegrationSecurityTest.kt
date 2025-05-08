/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.test

import org.hamcrest.Matchers.emptyOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.services.test.config.DatabaseTestConfiguration
import ru.romanow.services.test.config.KeycloakConfiguration
import java.net.URI

@ActiveProfiles("common", "test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability
@Import(value = [DatabaseTestConfiguration::class, KeycloakConfiguration::class])
internal class IntegrationSecurityTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

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
    fun `when request to permit endpoints then success`(path: String) {
        mockMvc.get("/manage/$path")
            .andExpect {
                status { isOk() }
                content { string(not(emptyOrNullString())) }
            }
    }

    @Test
    fun `when request to secured endpoints then success`() {
        mockMvc.get("/manage/info")
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `when request to secured endpoints with auth then success`() {
        mockMvc.get("/manage/info") { headers { setBasicAuth("management", "passwd") } }
            .andExpect {
                status { isOk() }
                content { string(not(emptyOrNullString())) }
            }
    }

    @Test
    fun `when request to secured api with auth then success`() {
        val type = object : ParameterizedTypeReference<Map<String, String>>() {}
        val response = builder.build()
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
            .block()!!

        mockMvc.get("/api/echo?message=test") {
            headers { header(HttpHeaders.AUTHORIZATION, "Bearer ${response["access_token"]}") }
        }
            .andExpect {
                status { isOk() }
                content { string("test - $LOGIN") }
            }
    }

    @Test
    fun `when request to secured api then unauthorized`() {
        mockMvc.get("/api/echo?message=test")
            .andExpect { status { isUnauthorized() } }
    }

    @RestController
    @SpringBootApplication
    internal class TestApplication {

        @GetMapping("/api/echo")
        fun echo(token: JwtAuthenticationToken, @RequestParam message: String): String {
            return "$message - ${token.token.claims["preferred_username"]}"
        }
    }

    companion object {
        private const val LOGIN = "admin"
        private const val PASSWORD = "admin"
    }
}
