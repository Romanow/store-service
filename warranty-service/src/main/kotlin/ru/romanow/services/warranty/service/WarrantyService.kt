/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.service

import ru.romanow.services.warranty.model.WarrantyStatusResponse
import java.util.*

interface WarrantyService {
    fun orderWarrantyStatus(orderUid: UUID): List<WarrantyStatusResponse>
}
