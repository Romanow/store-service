/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import ru.romanow.services.store.model.DetailedOrderResponse
import ru.romanow.services.store.model.OrderResponse
import ru.romanow.services.store.model.WarrantyRequest
import ru.romanow.services.store.model.WarrantyResponse
import java.util.*

interface OrderManagementService {
    fun orders(userId: String): List<OrderResponse>
    fun orderByUid(userId: String, orderUid: UUID): DetailedOrderResponse
    fun purchase(userId: String, items: List<String>): UUID
    fun warrantyRequest(userId: String, orderUid: UUID, request: List<WarrantyRequest>): List<WarrantyResponse>
    fun cancel(userId: String, orderUid: UUID)
}
