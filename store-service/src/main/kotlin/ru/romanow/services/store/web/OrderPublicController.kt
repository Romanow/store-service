/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.web

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import ru.romanow.services.store.model.DetailedItemInfo
import ru.romanow.services.store.model.DetailedOrderResponse
import ru.romanow.services.store.model.OrderResponse
import ru.romanow.services.store.service.OrderManagementService
import ru.romanow.services.warranty.model.WarrantyRequest
import ru.romanow.services.warranty.model.WarrantyResponse
import java.util.*

// @formatter:off
@Suppress("ktlint:standard:max-line-length")
@RestController
@RequestMapping("/api/public/v1/orders")
class OrderPublicController(private val orderManagementService: OrderManagementService): ApiController {

    override fun orders(authenticationToken: JwtAuthenticationToken?): List<OrderResponse> {
        val userId = extractUserId(authenticationToken)
        return orderManagementService.orders(userId)
    }

    override fun orderByUid(authenticationToken: JwtAuthenticationToken?, orderUid: UUID): DetailedOrderResponse {
        val userId = extractUserId(authenticationToken)
        return orderManagementService.orderByUid(userId, orderUid)
    }

    @PostMapping("/purchase")
    fun warrantyRequest(
        @Valid @RequestBody request: WarrantyRequest,
        authenticationToken: JwtAuthenticationToken?
    ): ResponseEntity<Void> {
        val userId = extractUserId(authenticationToken)
        val orderUid = orderManagementService.purchase(userId)
        return ResponseEntity.created(
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/purchase")
                .path("/{orderUid}")
                .buildAndExpand(orderUid)
                .toUri()
        ).build()
    }

    @PostMapping("/{orderUid}/warranty")
    fun warrantyRequest(
        @PathVariable orderUid: UUID,
        @Valid @RequestBody request: WarrantyRequest,
        authenticationToken: JwtAuthenticationToken?
    ): WarrantyResponse {
        val userId = extractUserId(authenticationToken)
        return orderManagementService.warrantyRequest(userId, orderUid)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderUid}/cancel")
    fun cancel(@PathVariable orderUid: UUID, authenticationToken: JwtAuthenticationToken?) {
        val userId = extractUserId(authenticationToken)
        orderManagementService.cancel(userId, orderUid)
    }

    private fun extractUserId(jwt: JwtAuthenticationToken?) =
        if (jwt != null) (jwt.token.claims["sub"] as String).substringAfter("|") else USER_UID

    companion object {
        private val USER_UID = UUID.fromString("28946ba3-bfd6-4087-9ffb-2a7520d78562").toString()
    }
}
// @formatter:on
