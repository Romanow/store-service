/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.model

import java.util.*

data class ItemResponse(
    var uid: UUID? = null,
    var availableCount: Int? = null,
    var name: String? = null,
    var description: String? = null,
    var manufacturer: String? = null,
    var imageUrl: String? = null
)
