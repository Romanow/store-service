/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import ru.romanow.services.store.domain.Order
import java.util.*

interface OrderService {
    fun orders(userId: String): List<Order>
    fun orderByUid(orderUid: UUID): Order
    fun create(userId: String, items: List<String>): Order
}
