package ru.romanow.services.gateway

import org.hamcrest.Matchers.emptyOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureObservability
internal class GatewayApplicationTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @ParameterizedTest
    @ValueSource(strings = ["health", "prometheus"])
    fun testPermitAllOnManageEndpoints(path: String) {
        webClient.get()
            .uri("/manage/$path")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String::class.java).value(not(emptyOrNullString()))
    }

    @Test
    fun testNotAuthorizedOnManageEndpoints() {
        webClient.get()
            .uri("/manage/info")
            .exchange()
            .expectStatus().isUnauthorized()
    }

    @Test
    fun testSuccessOnManageEndpoints() {
        webClient.get()
            .uri("/manage/info")
            .headers { it.setBasicAuth("management", "passwd") }
            .exchange()
            .expectStatus().isOk()
            .expectBody(String::class.java).value(not(emptyOrNullString()))
    }

//    companion object {
//
//        @JvmStatic
//        @RegisterExtension
//        var wm = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build()
//    }
}
