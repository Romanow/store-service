/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import java.util.*

interface WarehouseClient {
    fun items(names: List<String>): Optional<List<*>>
}
