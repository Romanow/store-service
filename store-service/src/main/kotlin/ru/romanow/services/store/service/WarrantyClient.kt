/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import java.util.*

interface WarrantyClient {
    fun warrantyStatus(orderUid: UUID): Optional<List<*>>
}
