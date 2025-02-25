package ru.romanow.inst.services.store.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import ru.romanow.inst.services.common.model.ErrorResponse
import ru.romanow.inst.services.store.model.*
import ru.romanow.inst.services.store.service.StoreService
import java.util.*
import javax.validation.Valid

// @formatter:off
@Suppress("ktlint:standard:max-line-length")
@Tag(name = "Сервис Магазина")
@RestController
@RequestMapping("/api/v1/store")
class StoreController(
    private val storeService: StoreService
) {

    @Operation(summary = "Информация о заказах пользователя")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Информация о заказах"),
            ApiResponse(responseCode = "404", description = "Пользователь не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "422", description = "Запрос к другой системе завершился с ошибкой", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @GetMapping("/orders", produces = ["application/json"])
    fun orders(authenticationToken: JwtAuthenticationToken?): UserOrdersResponse {
        val userId = extractUserId(authenticationToken)
        return storeService.findUserOrders(userId)
    }

    @Operation(summary = "Информация о заказе пользователя")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Информация о заказе"),
            ApiResponse(responseCode = "404", description = "Пользователь не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "422", description = "Запрос к другой системе завершился с ошибкой", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @GetMapping("/{orderUid}", produces = ["application/json"])
    fun orders(authenticationToken: JwtAuthenticationToken?, @PathVariable orderUid: UUID): UserOrderResponse {
        val userId = extractUserId(authenticationToken)
        return storeService.findUserOrder(userId, orderUid)
    }

    @Operation(summary = "Создать заказ")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Заказ создан"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "404", description = "Пользователь не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "409", description = "Item not available", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "422", description = "Запрос к другой системе завершился с ошибкой", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @PostMapping("/purchase", consumes = ["application/json"])
    fun purchase(
        @Valid @RequestBody request: PurchaseRequest,
        authenticationToken: JwtAuthenticationToken?
    ): ResponseEntity<Void> {
        val userId = extractUserId(authenticationToken)
        val orderUid: UUID = storeService.makePurchase(userId, request)
        val uri = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{orderUid}")
            .buildAndExpand(orderUid)
            .toUri()

        return ResponseEntity.created(uri).build()
    }

    @Operation(summary = "Отмена заказа")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Заказ успешно отменен"),
            ApiResponse(responseCode = "404", description = "Пользователь не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "422", description = "Запрос к другой системе завершился с ошибкой", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderUid}/refund")
    fun refund(@PathVariable orderUid: UUID, authenticationToken: JwtAuthenticationToken?) {
        val userId = extractUserId(authenticationToken)
        storeService.refundPurchase(userId, orderUid)
    }

    @Operation(summary = "Запрос гарантии")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Результат запроса по гарантии"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "404", description = "Пользователь не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "422", description = "Запрос к другой системе завершился с ошибкой", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @PostMapping("/{orderUid}/warranty", consumes = ["application/json"], produces = ["application/json"])
    fun warranty(
        @PathVariable orderUid: UUID,
        @Valid @RequestBody request: WarrantyRequest,
        authenticationToken: JwtAuthenticationToken?
    ): WarrantyResponse {
        val userId = extractUserId(authenticationToken)
        return storeService.warrantyRequest(userId, orderUid, request)
    }

    private fun extractUserId(jwt: JwtAuthenticationToken?) =
        if (jwt != null) (jwt.token.claims["sub"] as String).substringAfter("|") else USER_UID

    companion object {
        private val USER_UID = UUID.fromString("28946ba3-bfd6-4087-9ffb-2a7520d78562").toString()
    }
}
// @formatter:on
