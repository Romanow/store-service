package ru.romanow.services.gateway.web

import io.swagger.v3.core.util.Yaml
import io.swagger.v3.oas.models.OpenAPI
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import ru.romanow.services.gateway.config.KeycloakConfiguration

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureWebTestClient
@Import(KeycloakConfiguration::class)
class OpenApiControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `when generate aggregated OpenApi then success`() {
        val expected = ClassPathResource(EXPECTED_OPENAPI).inputStream.readAllBytes().decodeToString()
        val expectedOpenApi = Yaml.mapper().readValue(expected, OpenAPI::class.java)

        val actual = webClient.get()
            .uri("/api/v1/openapi")
            .headers { it.setBasicAuth("management", "passwd") }
            .exchange()
            .expectStatus().isOk()
            .returnResult(String::class.java)
            .responseBodyContent

        println(String(actual))
        val actualOpenApi = Yaml.mapper().readValue(actual, OpenAPI::class.java)
//        assertThat(actualOpenApi).isEqualTo(expectedOpenApi)
    }

    companion object {
        private const val EXPECTED_OPENAPI = "target/expected.yml"
    }
}
