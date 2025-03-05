/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.model

open class ItemInfo(
    val name: String? = null,
    val count: Int? = null
)

class DetailedItemInfo(
    name: String? = null,
    count: Int? = null,
) : ItemInfo(name, count)
