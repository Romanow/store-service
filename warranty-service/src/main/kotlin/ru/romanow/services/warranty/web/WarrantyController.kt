/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.web

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.romanow.services.warranty.model.WarrantyItem
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
        @Valid @RequestBody items: List<WarrantyItem>
    ): ResponseEntity<Unit> {
        warrantyService.start(orderUid, items)
        return ResponseEntity.noContent().build()
    }

    override fun warrantyRequest(
        @PathVariable("orderUid") orderUid: UUID,
        @Valid @RequestBody items: List<WarrantyItem>
    ): ResponseEntity<List<WarrantyResponse>> =
        ResponseEntity.ok(warrantyService.warrantyRequest(orderUid, items))

    override fun stop(@PathVariable("orderUid") orderUid: UUID): ResponseEntity<Unit> {
        warrantyService.stop(orderUid)
        return ResponseEntity.noContent().build()
    }
}
