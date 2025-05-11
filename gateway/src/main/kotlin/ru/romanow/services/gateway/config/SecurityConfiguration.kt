/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.config

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerReactiveAuthenticationManagerResolver.fromTrustedIssuers
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import ru.romanow.services.gateway.properties.ActuatorSecurityProperties

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Order(FIRST)
    @ConditionalOnProperty("oauth2.security.enabled", havingValue = "true", matchIfMissing = true)
    fun managementSecurityFilterChain(
        http: ServerHttpSecurity,
        properties: ActuatorSecurityProperties
    ): SecurityWebFilterChain {
        return http.invoke {
            securityMatcher(
                OrServerWebExchangeMatcher(
                    EndpointRequest.toAnyEndpoint(),
                    PathPatternParserServerWebExchangeMatcher("/api/v1/openapi")
                )
            )
            authorizeExchange {
                authorize("/manage/prometheus", permitAll)
                authorize("/manage/health/**", permitAll)
                authorize(anyExchange, hasRole(properties.role))
            }
            sessionManagement { STATELESS }
            formLogin { disable() }
            csrf { disable() }
            httpBasic { }
        }
    }

    @Bean
    @Order(SECOND)
    @ConditionalOnProperty("oauth2.security.enabled", havingValue = "true", matchIfMissing = true)
    fun protectedResourceSecurityFilterChain(
        http: ServerHttpSecurity, properties: OAuth2ClientProperties
    ): SecurityWebFilterChain {
        return http.invoke {
            authorizeExchange {
                authorize("/oauth2/authorization/**", permitAll)
                authorize("/login/oauth2/code/**", permitAll)
                authorize("/api/public/**", permitAll)
                authorize(PathPatternParserServerWebExchangeMatcher("/**", OPTIONS), permitAll)
                authorize(anyExchange, authenticated)
            }
            oauth2ResourceServer {
                authenticationManagerResolver = fromTrustedIssuers(properties.provider.map { it.value.issuerUri })
            }
            oauth2Login {
                authenticationSuccessHandler = RedirectServerAuthenticationSuccessHandler("/callback")
            }
            exceptionHandling {
                authenticationEntryPoint = HttpBasicServerAuthenticationEntryPoint()
            }
            csrf { disable() }
            cors { disable() }
        }
    }

    @Bean
    @Order(SECOND)
    @ConditionalOnProperty("oauth2.security.enabled", havingValue = "false")
    fun disabledSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.invoke {
            securityMatcher(PathPatternParserServerWebExchangeMatcher("/**"))
            authorizeExchange { authorize(anyExchange, permitAll) }
            csrf { disable() }
            cors { disable() }
        }
    }

    @Bean
    fun users(properties: ActuatorSecurityProperties): MapReactiveUserDetailsService {
        val user = User.builder()
            .username(properties.user)
            .password(passwordEncoder().encode(properties.passwd))
            .roles(properties.role)
            .build()
        return MapReactiveUserDetailsService(user)
    }

    companion object {
        private const val FIRST = 1
        private const val SECOND = 2
    }
}
