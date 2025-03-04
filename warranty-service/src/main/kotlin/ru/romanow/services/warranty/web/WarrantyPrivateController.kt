/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.web

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.romanow.services.warranty.service.WarrantyService
import java.util.*

// @formatter:off
@Tag(name = "Гарантийный сервис")
@RestController
@RequestMapping("/api/private/v1")
class WarrantyPrivateController(
    private var warrantyService: WarrantyService
) {

    @GetMapping("/{orderUid}")
    fun orderWarrantyStatus(@PathVariable orderUid: UUID) =
        warrantyService.orderWarrantyStatus(orderUid)
}
// @formatter:on
