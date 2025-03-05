/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.web

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.romanow.services.warehouse.service.ItemService

// @formatter:off
@Suppress("ktlint:standard:max-line-length")
@Tag(name = "Сервис склада")
@RestController
@RequestMapping("/api/public/v1/items")
class WarehousePublicController(
    private val itemService: ItemService
) {

    @GetMapping
    fun items() = itemService.items()
}
// @formatter:on
