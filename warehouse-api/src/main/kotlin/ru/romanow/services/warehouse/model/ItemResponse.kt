/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.model

data class ItemResponse(
    var name: String? = null,
    var availableCount: Int? = null,
    var description: String? = null,
    var manufacturer: String? = null,
    var imageUrl: String? = null
)
