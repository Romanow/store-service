/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.service

import ru.romanow.services.warehouse.model.ItemRequest
import ru.romanow.services.warehouse.model.ItemResponse
import java.util.*

interface ItemService {
    fun items(): List<ItemResponse>
    fun items(names: List<String>): Map<String, ItemResponse>
    fun takeItems(orderUid: UUID, request: List<ItemRequest>): ItemResponse
    fun returnItems(orderUid: UUID)
}
