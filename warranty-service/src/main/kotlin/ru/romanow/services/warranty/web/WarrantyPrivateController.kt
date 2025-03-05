/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.web

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import ru.romanow.services.warranty.model.WarrantyRequest
import ru.romanow.services.warranty.service.WarrantyService
import java.util.*

// @formatter:off
@Suppress("ktlint:standard:max-line-length")
@Tag(name = "Гарантийный сервис")
@RestController
@RequestMapping("/api/private/v1/warranty")
class WarrantyPrivateController(
    private var warrantyService: WarrantyService
) {

    @GetMapping("/{orderUid}")
    fun warrantyStatus(@PathVariable orderUid: UUID) =
        warrantyService.warrantyStatus(orderUid)

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderUid}/start")
    fun start(@PathVariable orderUid: UUID, @RequestBody names: List<String>) {
        warrantyService.start(orderUid, names)
    }

    @PostMapping("/{orderUid}/request")
    fun warrantyRequest(@PathVariable orderUid: UUID, @Valid @RequestBody request: WarrantyRequest) =
        warrantyService.warrantyRequest(orderUid, request)

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderUid}/stop")
    fun stop(@PathVariable orderUid: UUID) {
        warrantyService.stop(orderUid)
    }
}
// @formatter:on
