package ru.romanow.inst.services.store.model

import java.util.*

data class UserOrderResponse(
    var orderUid: UUID,
    val date: String,
    var model: String? = null,
    var size: SizeChart? = null,
    var warrantyDate: String? = null,
    var warrantyStatus: WarrantyStatus? = null
)