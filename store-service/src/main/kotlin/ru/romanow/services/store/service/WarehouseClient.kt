/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import ru.romanow.services.warehouse.model.ItemResponse
import java.util.*

interface WarehouseClient {
    fun items(names: List<String>): Optional<List<ItemResponse>>
}
