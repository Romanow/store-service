/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.service

import ru.romanow.services.warehouse.models.ItemInfo
import java.util.*

interface WarehouseClient {
    fun items(names: Set<String>): Optional<List<ItemInfo>>
}
