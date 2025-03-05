/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import ru.romanow.services.store.model.DetailedItemInfo
import ru.romanow.services.store.model.ItemInfo
import ru.romanow.services.store.model.OrderResponse
import ru.romanow.services.warranty.model.WarrantyResponse
import java.util.*

interface OrderManagementService {
    fun orders(userId: String): List<OrderResponse<ItemInfo>>
    fun orderByUid(userId: String, orderUid: UUID): OrderResponse<DetailedItemInfo>
    fun purchase(userId: String): UUID
    fun warrantyRequest(userId: String, orderUid: UUID): WarrantyResponse
    fun cancel(userId: String, orderUid: UUID)
}
