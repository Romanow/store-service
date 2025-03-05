/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.model

import ru.romanow.services.warranty.model.WarrantyStatusResponse

open class ItemInfo(
    val name: String? = null,
    val count: Int? = null
)

class DetailedItemInfo(
    name: String? = null,
    count: Int? = null,
    val description: String? = null,
    val manufacturer: String? = null,
    val imageUrl: String? = null,
    val warranty: WarrantyStatusResponse? = null
) : ItemInfo(name, count)
