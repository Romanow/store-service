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

    override fun orderByUid(orderUid: UUID): DetailedOrderResponse {
        val order = orderService.orderByUid(orderUid)
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
        warehouseClient.take(items)
        val order = orderService.create(userId, items)
        warrantyClient.start(order.uid!!, items)
        return order.uid!!
    }

    override fun warrantyRequest(orderUid: UUID, items: List<String>): List<WarrantyResponse> {
        return warrantyClient.request(orderUid, items)
            .orElse(listOf())
            .map { WarrantyResponse(it.name, WarrantyStatus.valueOf(it.status.name), it.comment) }
    }

    override fun cancel(orderUid: UUID) {
        val order = orderService.orderByUid(orderUid)
        order.status = OrderStatus.CANCELED
        warrantyClient.stop(orderUid)
        warehouseClient.refund(order.items!!.map { it.name!! })
    }
}
