/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.web

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.romanow.services.warranty.model.WarrantyResponse
import ru.romanow.services.warranty.model.WarrantyStatusResponse
import ru.romanow.services.warranty.service.WarrantyService
import java.util.*

@RestController
class WarrantyController(
    private var warrantyService: WarrantyService
) : ApiController {

    override fun status(@PathVariable("orderUid") orderUid: UUID): ResponseEntity<List<WarrantyStatusResponse>> =
        ResponseEntity.ok(warrantyService.status(orderUid))

    override fun start(
        @PathVariable("orderUid") orderUid: UUID,
        @Valid @RequestBody requestBody: List<String>
    ): ResponseEntity<Unit> {
        warrantyService.start(orderUid, requestBody)
        return ResponseEntity.accepted().build()
    }

    override fun warrantyRequest(
        @PathVariable("orderUid") orderUid: UUID,
        @Valid @RequestBody requestBody: List<String>
    ): ResponseEntity<List<WarrantyResponse>> =
        ResponseEntity.ok(warrantyService.warrantyRequest(orderUid, requestBody))

    override fun stop(@PathVariable("orderUid") orderUid: UUID): ResponseEntity<Unit> {
        warrantyService.stop(orderUid)
        return ResponseEntity.accepted().build()
    }
}
