/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.web

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.romanow.services.warehouse.model.ItemRequest
import ru.romanow.services.warehouse.service.ItemService
import java.util.*

// @formatter:off
@Suppress("ktlint:standard:max-line-length")
@Tag(name = "Сервис склада")
@RestController
@RequestMapping("/api/private/v1/items")
class WarehousePrivateController(
    private val itemService: ItemService
) {
    @PutMapping
    fun items(@RequestBody names: List<String>) = itemService.items(names)

    @PostMapping("/{orderUid}/take")
    fun takeItems(@PathVariable orderUid: UUID, @Valid @RequestBody items: List<ItemRequest>) =
        itemService.takeItems(orderUid, items)

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderUid}/return")
    fun returnItems(@PathVariable orderUid: UUID) {
        itemService.returnItems(orderUid)
    }
}
// @formatter:on
