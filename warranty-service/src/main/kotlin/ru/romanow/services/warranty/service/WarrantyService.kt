/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.service

import ru.romanow.services.warranty.model.WarrantyResponse
import ru.romanow.services.warranty.model.WarrantyStatusResponse
import java.util.*

interface WarrantyService {
    fun status(orderUid: UUID): List<WarrantyStatusResponse>
    fun start(orderUid: UUID, items: List<String>)
    fun warrantyRequest(orderUid: UUID, items: List<String>): List<WarrantyResponse>
    fun stop(orderUid: UUID)
}
