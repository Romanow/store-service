/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import org.springframework.stereotype.Service
import ru.romanow.services.store.model.DetailedOrderResponse
import ru.romanow.services.store.model.OrderResponse
import ru.romanow.services.store.model.WarrantyRequest
import ru.romanow.services.store.model.WarrantyResponse
import ru.romanow.services.store.service.OrderManagementService
import ru.romanow.services.store.service.OrderService
import java.util.*

@Service
internal class OrderManagementServiceImpl(
    private val orderService: OrderService,
) : OrderManagementService {

    override fun orders(userId: String): List<OrderResponse> =
        orderService.orders(userId)
            .map {
                OrderResponse(
                    orderUid = it.uid!!,
                    userId = userId,
                    status = it.status!!,
                    orderDate = it.createdDate!!,
                    items = it.items?.map { i -> i.name!! }.orEmpty(),
                )
            }


    override fun orderByUid(userId: String, orderUid: UUID): DetailedOrderResponse {
        TODO("Not yet implemented")
    }

    override fun purchase(userId: String, items: List<String>): UUID {
        TODO("Not yet implemented")
    }

    override fun warrantyRequest(
        userId: String,
        orderUid: UUID,
        request: List<WarrantyRequest>
    ): List<WarrantyResponse> {
        TODO("Not yet implemented")
    }

    override fun cancel(userId: String, orderUid: UUID) {
        TODO("Not yet implemented")
    }
}
