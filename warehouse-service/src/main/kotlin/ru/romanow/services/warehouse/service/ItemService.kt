/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.service

import ru.romanow.services.warehouse.model.ItemResponse

interface ItemService {
    fun items(): List<ItemResponse>
}
