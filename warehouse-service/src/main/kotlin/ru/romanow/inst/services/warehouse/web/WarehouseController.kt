package ru.romanow.inst.services.warehouse.web

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.romanow.inst.services.common.model.ErrorResponse
import ru.romanow.inst.services.warehouse.model.ItemInfoResponse
import ru.romanow.inst.services.warehouse.model.ItemResponse
import ru.romanow.inst.services.warehouse.model.OrderItemRequest
import ru.romanow.inst.services.warehouse.model.OrderItemResponse
import ru.romanow.inst.services.warehouse.service.WarehouseService
import ru.romanow.inst.services.warehouse.service.WarrantyService
import ru.romanow.inst.services.warranty.model.OrderWarrantyRequest
import ru.romanow.inst.services.warranty.model.OrderWarrantyResponse
import java.util.*
import javax.validation.Valid

// @formatter:off
@Suppress("ktlint:standard:max-line-length")
@Tag(name = "Сервис Склада")
@RestController
@RequestMapping("/api/v1/warehouse")
class WarehouseController(
    private val warehouseService: WarehouseService,
    private val warrantyService: WarrantyService
) {

    @Operation(summary = "Получить список товаров в наличии")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Информация о товарах")])
    @GetMapping(produces = ["application/json"])
    private fun items(): List<ItemResponse> = warehouseService.items()

    @Operation(summary = "Получить информацию товаре")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Информация о товаре"),
            ApiResponse(responseCode = "404", description = "Товар не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @GetMapping(value = ["/{orderItemUid}"], produces = ["application/json"])
    private fun item(@PathVariable orderItemUid: UUID): ItemInfoResponse {
        return warehouseService.getItemInfo(orderItemUid)
    }

    @Operation(summary = "Добавить товар в заказ")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Товар добавлен в заказ"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "404", description = "Товар не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "409", description = "Товар недоступен", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun takeItem(@Valid @RequestBody request: OrderItemRequest): OrderItemResponse {
        return warehouseService.takeItem(request)
    }

    @Operation(summary = "Возврат товара на склад")
    @ApiResponse(responseCode = "204", description = "Товар возвращен")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderItemUid}")
    fun returnItem(@PathVariable orderItemUid: UUID) {
        warehouseService.returnItem(orderItemUid)
    }

    @Operation(summary = "Запрос гарантии по товару")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Решение по гарантии"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "404", description = "Товар не найден", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "422", description = "Запрос к другой системе завершился с ошибкой", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
        ]
    )
    @PostMapping("/{orderItemUid}/warranty")
    fun warranty(
        @PathVariable orderItemUid: UUID,
        @Valid @RequestBody request: OrderWarrantyRequest
    ): OrderWarrantyResponse {
        return warrantyService.warrantyRequest(orderItemUid, request)
    }
}
// @formatter:on
