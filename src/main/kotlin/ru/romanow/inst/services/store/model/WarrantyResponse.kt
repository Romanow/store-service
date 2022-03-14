package ru.romanow.inst.services.store.model

import java.util.*

data class WarrantyResponse(
    var orderUid: UUID,
    val warrantyDate: String,
    val decision: String
)