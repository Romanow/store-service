/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.model

import java.time.LocalDateTime
import java.util.*

data class OrderResponse<T : ItemInfo>(
    var orderUid: UUID? = null,
    var userId: String? = null,
    var status: OrderStatus? = null,
    var orderDate: LocalDateTime? = null,
    var items: List<T>? = null
)
