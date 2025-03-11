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
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import ru.romanow.services.test.config.DatabaseTestConfiguration
import ru.romanow.services.test.config.KeycloakConfiguration

@ActiveProfiles("common", "test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureObservability
@Import(value = [DatabaseTestConfiguration::class, KeycloakConfiguration::class])
internal class IntegrationSecurityTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @ParameterizedTest
    @ValueSource(strings = ["health", "prometheus"])
    fun testPermitAllOnManageEndpoints(path: String) {
        mockMvc.get("/manage/$path")
            .andExpect {
                status { isOk() }
                content { string(not(emptyOrNullString())) }
            }
    }

    @Test
    fun testNotAuthorizedOnManageEndpoints() {
        mockMvc.get("/manage/info")
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun testSuccessOnManageEndpoints() {
        mockMvc.get("/manage/info") { headers { setBasicAuth("management", "passwd") } }
            .andExpect {
                status { isOk() }
                content { string(not(emptyOrNullString())) }
            }
    }

    @SpringBootApplication
    internal class TestApplication
}
