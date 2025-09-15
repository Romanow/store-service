package ru.romanow.services.gateway.filters

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class RedirectUriFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (exchange.request.uri.path.startsWith("/oauth2/authorization/")) {
            val redirectUri = exchange.request.queryParams.getFirst("redirect_uri")
            if (redirectUri != null) {
                return exchange.session
                    .doOnNext { it.attributes["redirect_uri"] = redirectUri }
                    .then(chain.filter(exchange))
            }
        }
        return chain.filter(exchange)
    }
}
