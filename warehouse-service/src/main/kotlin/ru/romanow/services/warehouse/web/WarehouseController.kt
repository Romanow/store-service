/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.web

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.romanow.services.warehouse.service.ItemService

@Tag(name = "Сервис склада")
@RestController
@RequestMapping("/api/v1")
class WarehouseController(
    private val itemService: ItemService
) {

    @GetMapping
    fun items() = itemService.items()
}
