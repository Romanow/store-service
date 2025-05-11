/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.service

import ru.romanow.services.warehouse.model.ItemInfo

interface ItemService {
    fun availableItems(): List<ItemInfo>
    fun items(names: List<String>): List<ItemInfo>
    fun takeItems(names: List<String>)
    fun returnItems(names: List<String>)
}
