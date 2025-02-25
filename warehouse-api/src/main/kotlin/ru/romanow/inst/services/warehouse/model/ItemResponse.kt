package ru.romanow.inst.services.warehouse.model

data class ItemResponse(
    val model: String,
    val size: String,
    val availableCount: Int
)
