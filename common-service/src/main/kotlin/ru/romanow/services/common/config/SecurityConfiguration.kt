/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.common.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver.fromTrustedIssuers
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import ru.romanow.services.common.properties.ActuatorSecurityProperties

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Order(FIRST)
    @ConditionalOnProperty("oauth2.security.enabled", havingValue = "true", matchIfMissing = true)
    fun protectedResourceSecurityFilterChain(
        http: HttpSecurity, properties: OAuth2ClientProperties
    ): SecurityFilterChain {
        val sources = properties.provider.map { it.value.issuerUri }
        return http
            .securityMatcher("/api/**")
            .authorizeHttpRequests {
                it.requestMatchers(OPTIONS).permitAll()
                it.requestMatchers("/api/public/**").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer {
                it.authenticationManagerResolver(fromTrustedIssuers(sources))
            }
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(UNAUTHORIZED))
            }
            .csrf { it.disable() }
            .cors { }
            .build()
    }

    @Bean
    @Order(SECOND)
    @ConditionalOnProperty("oauth2.security.enabled", havingValue = "true", matchIfMissing = true)
    fun managementSecurityFilterChain(http: HttpSecurity, properties: ActuatorSecurityProperties): SecurityFilterChain {
        return http
            .securityMatcher("/manage/**")
            .authorizeHttpRequests {
                it.requestMatchers("/manage/health/**", "/manage/prometheus")
                    .permitAll()
                    .anyRequest().hasRole(properties.role)
            }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(STATELESS) }
            .httpBasic {}
            .build()
    }

    @Bean
    @Order(SECOND)
    @ConditionalOnProperty("oauth2.security.enabled", havingValue = "false")
    fun disabledSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .securityMatcher("/**")
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .csrf { it.disable() }
            .cors { it.disable() }
            .build()
    }

    @Bean
    fun users(properties: ActuatorSecurityProperties): UserDetailsService {
        val user = User.builder()
            .username(properties.user)
            .password(passwordEncoder().encode(properties.passwd))
            .roles(properties.role)
            .build()
        return InMemoryUserDetailsManager(user)
    }

    companion object {
        private const val FIRST = 1
        private const val SECOND = 2
    }
}
