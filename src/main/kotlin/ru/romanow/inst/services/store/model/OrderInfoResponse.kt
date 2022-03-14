package ru.romanow.inst.services.store.model

import java.util.*

data class OrderInfoResponse(
    var orderUid: UUID,
    val orderDate: String,
    val itemUid: UUID,
    val status: PaymentStatus
)