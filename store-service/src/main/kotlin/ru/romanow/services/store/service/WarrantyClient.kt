/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import ru.romanow.services.warranty.models.WarrantyResponse
import ru.romanow.services.warranty.models.WarrantyStatusResponse
import java.util.*

interface WarrantyClient {
    fun status(orderUid: UUID): Optional<List<WarrantyStatusResponse>>
    fun request(orderUid: UUID, items: List<String>): Optional<List<WarrantyResponse>>
    fun start(orderUid: UUID, items: List<String>)
    fun stop(orderUid: UUID)
}
