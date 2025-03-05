/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import org.springframework.stereotype.Service
import ru.romanow.services.store.model.DetailedItemInfo
import ru.romanow.services.store.model.ItemInfo
import ru.romanow.services.store.model.OrderResponse
import ru.romanow.services.warranty.model.WarrantyResponse
import java.util.*

@Service
class OrderManagementServiceImpl(
    private val orderService: OrderService
) : OrderManagementService {

    override fun orders(userId: String): List<OrderResponse<ItemInfo>> {
        TODO("Not yet implemented")
    }

    override fun orderByUid(userId: String, orderUid: UUID): OrderResponse<DetailedItemInfo> {
        TODO("Not yet implemented")
    }

    override fun purchase(userId: String): UUID {
        TODO("Not yet implemented")
    }

    override fun warrantyRequest(userId: String, orderUid: UUID): WarrantyResponse {
        TODO("Not yet implemented")
    }

    override fun cancel(userId: String, orderUid: UUID) {
        TODO("Not yet implemented")
    }
}
