/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.test.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

typealias CustomPostgresContainer = PostgreSQLContainer<*>

@TestConfiguration
class DatabaseTestConfiguration {

    @Bean
    @ServiceConnection
    fun postgres(): PostgreSQLContainer<*> {
        return CustomPostgresContainer(POSTGRES_IMAGE)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withDatabaseName(DATABASE_NAME)
    }

    companion object {
        private const val POSTGRES_IMAGE = "postgres:15-alpine"
        private const val DATABASE_NAME = "common"
        private const val USERNAME = "program"
        private const val PASSWORD = "test"
    }
}
