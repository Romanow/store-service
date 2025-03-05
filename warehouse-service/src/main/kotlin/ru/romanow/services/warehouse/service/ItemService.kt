/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.service

import ru.romanow.services.warehouse.model.ItemRequest
import ru.romanow.services.warehouse.model.ItemResponse
import java.util.*

interface ItemService {
    fun items(): List<ItemResponse>
    fun items(names: List<String>): List<ItemResponse>
    fun takeItems(orderUid: UUID, request: List<ItemRequest>)
    fun returnItems(orderUid: UUID)
}
