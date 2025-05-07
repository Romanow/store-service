/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.web

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import ru.romanow.services.store.model.DetailedOrderResponse
import ru.romanow.services.store.model.OrderResponse
import ru.romanow.services.store.model.WarrantyRequest
import ru.romanow.services.store.model.WarrantyResponse
import ru.romanow.services.store.service.OrderManagementService
import java.util.*

@RestController
class OrderController(
    private val orderManagementService: OrderManagementService
) : ApiController {

    override fun orders(authenticationToken: JwtAuthenticationToken?): ResponseEntity<List<OrderResponse>> {
        val userId = extractUserId(authenticationToken)
        return ResponseEntity.ok(orderManagementService.orders(userId))
    }

    override fun orderByUid(
        authenticationToken: JwtAuthenticationToken?,
        orderUid: UUID
    ): ResponseEntity<DetailedOrderResponse> {
        val userId = extractUserId(authenticationToken)
        return ResponseEntity.ok(orderManagementService.orderByUid(userId, orderUid))
    }

    @PostMapping("/purchase")
    override fun purchase(
        authenticationToken: JwtAuthenticationToken?,
        @Valid @RequestBody requestBody: List<String>
    ): ResponseEntity<Unit> {
        val userId = extractUserId(authenticationToken)
        val orderUid = orderManagementService.purchase(userId, requestBody)
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
    override fun warranty(
        authenticationToken: JwtAuthenticationToken?,
        @PathVariable orderUid: UUID,
        @Valid @RequestBody warrantyRequest: List<WarrantyRequest>,
    ): ResponseEntity<List<WarrantyResponse>> {
        val userId = extractUserId(authenticationToken)
        return ResponseEntity.ok(orderManagementService.warrantyRequest(userId, orderUid, warrantyRequest))
    }

    override fun cancel(
        authenticationToken: JwtAuthenticationToken?,
        @PathVariable orderUid: UUID
    ): ResponseEntity<Unit> {
        val userId = extractUserId(authenticationToken)
        orderManagementService.cancel(userId, orderUid)
        return ResponseEntity.noContent().build()
    }

    private fun extractUserId(jwt: JwtAuthenticationToken?) =
        if (jwt != null) (jwt.token.claims["sub"] as String).substringAfter("|") else USER_UID

    companion object {
        private val USER_UID = UUID.fromString("28946ba3-bfd6-4087-9ffb-2a7520d78562").toString()
    }
}
