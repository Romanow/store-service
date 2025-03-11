/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.test.config

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry

@TestConfiguration
class KeycloakConfiguration {

    @Bean
    fun keycloak(registry: DynamicPropertyRegistry): KeycloakContainer {
        val keycloak = KeycloakContainer(KEYCLOAK_IMAGE).withRealmImportFile(REALM_FILE)
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri") {
            "${keycloak.authServerUrl}/realms/$REALM_NAME"
        }
        return keycloak
    }

    companion object {
        private const val KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.0"
        private const val REALM_FILE: String = "/data/realm.json"
        private const val REALM_NAME = "master"
    }
}
