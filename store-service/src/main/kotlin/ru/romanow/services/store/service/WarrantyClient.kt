/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import ru.romanow.services.warranty.models.WarrantyStatusResponse
import java.util.*

interface WarrantyClient {
    fun status(orderUid: UUID): Optional<List<WarrantyStatusResponse>>
}
