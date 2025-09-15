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
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerReactiveAuthenticationManagerResolver.fromTrustedIssuers
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import ru.romanow.services.gateway.filters.RedirectUriFilter
import ru.romanow.services.gateway.properties.ActuatorSecurityProperties
import ru.romanow.services.gateway.services.AuthenticationSuccessHandler

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
        return http {
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
        http: ServerHttpSecurity,
        properties: OAuth2ClientProperties,
        redirectUriFilter: RedirectUriFilter,
        successHandler: AuthenticationSuccessHandler
    ): SecurityWebFilterChain {
        return http {
            addFilterBefore(redirectUriFilter, SecurityWebFiltersOrder.HTTP_HEADERS_WRITER)
            authorizeExchange {
                authorize("/oauth2/authorization/**", permitAll)
                authorize("/login/oauth2/code/**", permitAll)
                authorize("/*/api/public/**", permitAll)
                authorize(pathMatchers(OPTIONS, "/**"), permitAll)
                authorize(anyExchange, authenticated)
            }
            oauth2ResourceServer {
                authenticationManagerResolver = fromTrustedIssuers(properties.provider.map { it.value.issuerUri })
            }
            oauth2Login {
                authenticationSuccessHandler = successHandler
            }
            exceptionHandling {
                authenticationEntryPoint = HttpBasicServerAuthenticationEntryPoint()
            }

            csrf { disable() }
        }
    }

    @Bean
    @Order(SECOND)
    @ConditionalOnProperty("oauth2.security.enabled", havingValue = "false")
    fun disabledSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            authorizeExchange { authorize(anyExchange, permitAll) }
            csrf { disable() }
        }
    }

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("http://localhost:*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Location", "Authorization", "Content-Type", "Origin")
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return CorsWebFilter(source)
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
