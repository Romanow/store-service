package ru.romanow.inst.services.order.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.romanow.inst.services.common.model.ErrorResponse
import ru.romanow.inst.services.order.model.CreateOrderRequest
import ru.romanow.inst.services.order.model.CreateOrderResponse
import ru.romanow.inst.services.order.model.OrderInfoResponse
import ru.romanow.inst.services.order.model.OrdersInfoResponse
import ru.romanow.inst.services.order.service.OrderManagementService
import ru.romanow.inst.services.order.service.OrderService
import ru.romanow.inst.services.warranty.model.OrderWarrantyRequest
import ru.romanow.inst.services.warranty.model.OrderWarrantyResponse
import java.util.*
import javax.validation.Valid

// @formatter:off
@Suppress("ktlint:standard:max-line-length")
@Tag(name = "Сервис Заказов")
@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: OrderService,
    private val orderManagementService: OrderManagementService
) {

    @Operation(summary = "Информация о заказе пользователя")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Информация о заказе"),
            ApiResponse(responseCode = "404", description = "Заказ не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @GetMapping("/{userId}/{orderUid}", produces = ["application/json"])
    fun userOrder(@PathVariable userId: String, @PathVariable orderUid: UUID): OrderInfoResponse {
        return orderService.getUserOrder(userId, orderUid)
    }

    @Operation(summary = "Информация о заказах пользователя")
    @ApiResponse(responseCode = "200", description = "Информация о заказах")
    @GetMapping("/{userId}", produces = ["application/json"])
    fun userOrders(@PathVariable userId: String): OrdersInfoResponse {
        return orderService.getUserOrders(userId)
    }

    @Operation(summary = "Создать заказ")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Заказ создан"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "409", description = "Товар недоступен", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "422", description = "Запрос к другой системе завершился с ошибкой", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @PostMapping(value = ["/{userId}"], consumes = ["application/json"], produces = ["application/json"])
    fun makeOrder(@PathVariable userId: String, @Valid @RequestBody request: CreateOrderRequest): CreateOrderResponse {
        return orderManagementService.makeOrder(userId, request)
    }

    @Operation(summary = "Отмена заказа")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Заказ успешно отменен"),
            ApiResponse(responseCode = "404", description = "Заказ не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "422", description = "Запрос к другой системе завершился с ошибкой", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = ["/{orderUid}"])
    private fun refundOrder(@PathVariable orderUid: UUID) {
        orderManagementService.refundOrder(orderUid)
    }

    @Operation(summary = "Запрос гарантии")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Результат запроса по гарантии"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "404", description = "Заказ не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "422", description = "Запрос к другой системе завершился с ошибкой", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @PostMapping(value = ["/{orderUid}/warranty"], consumes = ["application/json"], produces = ["application/json"])
    private fun warranty(
        @PathVariable orderUid: UUID,
        @Valid @RequestBody request: OrderWarrantyRequest
    ): OrderWarrantyResponse {
        return orderManagementService.useWarranty(orderUid, request)
    }
}
// @formatter:on
