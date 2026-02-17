package ru.romanow.services.gateway.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI
import java.time.Duration


@Component
class AuthenticationSuccessHandler(
    @Value("\${oauth2.login.success-redirect-uri}") private val successRedirectUri: String
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(filter: WebFilterExchange, authentication: Authentication): Mono<Void> {
        val exchange = filter.exchange
        if (authentication is OAuth2AuthenticationToken) {
            val accessToken = (authentication.principal as DefaultOidcUser).idToken.tokenValue
            if (accessToken != null) {
                val tokenCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                    .path("/")
                    .maxAge(Duration.ofHours(24))
                    .build()
                exchange.response.addCookie(tokenCookie)
            }
        }

        return exchange.session.flatMap {
            val redirectUri = it.attributes["redirect_uri"] as String? ?: successRedirectUri
            it.attributes.remove("redirect_uri")
            exchange.response.setStatusCode(HttpStatus.SEE_OTHER)
            exchange.response.headers.location = URI.create(redirectUri)
            exchange.response.setComplete()
        }
    }
}
