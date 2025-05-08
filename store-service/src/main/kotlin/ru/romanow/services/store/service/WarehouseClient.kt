/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import ru.romanow.services.warehouse.models.ItemInfo
import java.util.*

interface WarehouseClient {
    fun items(names: List<String>): Optional<List<ItemInfo>>
    fun take(items: List<String>)
    fun refund(items: List<String>)
}
