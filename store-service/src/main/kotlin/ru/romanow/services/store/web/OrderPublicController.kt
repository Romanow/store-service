/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.web

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

// @formatter:off
@Tag(name = "Магазин")
@RestController
@RequestMapping("/api/public/v1")
class OrderPublicController {

    @GetMapping("/orders")
    fun orders(authenticationToken: JwtAuthenticationToken?) {
        val userId = extractUserId(authenticationToken)
    }

    private fun extractUserId(jwt: JwtAuthenticationToken?) =
        if (jwt != null) (jwt.token.claims["sub"] as String).substringAfter("|") else USER_UID

    companion object {
        private val USER_UID = UUID.fromString("28946ba3-bfd6-4087-9ffb-2a7520d78562").toString()
    }
}
// @formatter:on
