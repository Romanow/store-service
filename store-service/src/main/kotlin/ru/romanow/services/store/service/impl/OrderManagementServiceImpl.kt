/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import org.springframework.stereotype.Service
import ru.romanow.services.store.model.*
import ru.romanow.services.store.service.OrderManagementService
import ru.romanow.services.store.service.OrderService
import ru.romanow.services.store.service.WarehouseClient
import ru.romanow.services.store.service.WarrantyClient
import java.util.*

@Service
internal class OrderManagementServiceImpl(
    private val orderService: OrderService,
    private val warrantyClient: WarrantyClient,
    private val warehouseClient: WarehouseClient
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
        val order = orderService.orderByUid(userId, orderUid)
        val names = order.items!!.map { it.name!! }
        val warrantyDetails = warrantyClient.status(orderUid).orElse(listOf()).associateBy { it.name }
        val itemDetails = warehouseClient.items(names).orElse(listOf()).associateBy { it.name }
        return DetailedOrderResponse(
            orderUid = order.uid!!,
            userId = order.createdUser!!,
            status = order.status!!,
            orderDate = order.createdDate!!,
            items = names.map {
                val details = itemDetails[it]!!
                val warranty = warrantyDetails[it]!!
                ItemInfo(
                    name = it,
                    description = details.description,
                    manufacturer = details.manufacturer,
                    imageUrl = details.imageUrl,
                    warranty = WarrantyStatusInfo(
                        status = WarrantyStatus.valueOf(warranty.status.name),
                        comment = warranty.comment,
                        warrantyStartDate = warranty.warrantyStartDate,
                        lastUpdateDate = warranty.lastUpdateDate
                    )
                )
            }
        )
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
