/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import ru.romanow.services.warranty.model.WarrantyStatusResponse
import java.util.*

interface WarrantyClient {
    fun warrantyStatus(orderUid: UUID): Optional<List<WarrantyStatusResponse>>
}
