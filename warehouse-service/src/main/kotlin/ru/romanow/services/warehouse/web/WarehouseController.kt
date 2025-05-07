/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.web

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.romanow.services.warehouse.model.ItemInfo
import ru.romanow.services.warehouse.service.ItemService

@RestController
class WarehouseController(
    private val itemService: ItemService
) : ApiController {
    override fun availableItems(): ResponseEntity<List<ItemInfo>> = ResponseEntity.ok(itemService.availableItems())

    override fun items(
        @NotNull @Valid @RequestParam(value = "names") names: List<String>
    ): ResponseEntity<List<ItemInfo>> =
        ResponseEntity.ok(itemService.items(names))

    override fun take(@Valid @RequestBody requestBody: List<String>): ResponseEntity<Unit> {
        itemService.takeItems(requestBody)
        return ResponseEntity.accepted().build()
    }

    override fun refund(@Valid @RequestBody requestBody: List<String>): ResponseEntity<Unit> {
        itemService.returnItems(requestBody)
        return ResponseEntity.noContent().build()
    }
}
