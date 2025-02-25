package ru.romanow.inst.services.warehouse.config

import com.zaxxer.hikari.HikariDataSource
import org.postgresql.Driver
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.PostgreSQLContainer

typealias CustomPostgresContainer = PostgreSQLContainer<*>

@TestConfiguration
class DatabaseTestConfiguration {
    @Bean(destroyMethod = "close")
    fun postgres(): PostgreSQLContainer<*> {
        val postgres = CustomPostgresContainer(POSTGRES_IMAGE)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)
            .withDatabaseName(DATABASE_NAME)
        postgres.start()
        return postgres
    }

    @Primary
    @DependsOn("postgres")
    @Bean(destroyMethod = "close")
    fun dataSource(): HikariDataSource {
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = postgres().jdbcUrl
        dataSource.username = USERNAME
        dataSource.password = PASSWORD
        dataSource.driverClassName = Driver::class.java.canonicalName
        return dataSource
    }

    companion object {
        private const val POSTGRES_IMAGE = "postgres:15-alpine"
        private const val DATABASE_NAME = "persons"
        private const val USERNAME = "program"
        private const val PASSWORD = "test"
    }
}
