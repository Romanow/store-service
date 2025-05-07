/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.CurrentDateTimeProvider
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.*


@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider", auditorAwareRef = "auditorAware")
class DatabaseConfiguration {

    @Bean
    fun dateTimeProvider(): DateTimeProvider {
        return CurrentDateTimeProvider.INSTANCE
    }

    @Bean
    fun auditorAware(): AuditorAware<String> {
        return AuditorAware<String> {
            val authentication = SecurityContextHolder.getContext().authentication
            return@AuditorAware if (authentication != null && authentication is JwtAuthenticationToken) {
                Optional.of((authentication.token.claims["sub"] as String).substringAfter("|"))
            } else {
                Optional.of(DEFAULT_USER)
            }
        }
    }

    companion object {
        private const val DEFAULT_USER = "system"
    }
}
