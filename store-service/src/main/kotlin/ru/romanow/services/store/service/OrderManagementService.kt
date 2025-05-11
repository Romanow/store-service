/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import ru.romanow.services.store.model.DetailedOrderResponse
import ru.romanow.services.store.model.OrderResponse
import ru.romanow.services.store.model.WarrantyResponse
import java.util.*

interface OrderManagementService {
    fun orders(userId: String): List<OrderResponse>
    fun orderByUid(orderUid: UUID): DetailedOrderResponse
    fun purchase(userId: String, items: List<String>): UUID
    fun warrantyRequest(orderUid: UUID, items: List<String>): List<WarrantyResponse>
    fun cancel(orderUid: UUID)
}
