/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import org.springframework.stereotype.Service
import ru.romanow.services.store.model.DetailedItemInfo
import ru.romanow.services.store.model.ItemInfo
import ru.romanow.services.store.model.OrderResponse
import ru.romanow.services.store.service.OrderManagementService
import ru.romanow.services.store.service.OrderService
import ru.romanow.services.store.service.WarehouseClient
import ru.romanow.services.store.service.WarrantyClient
import ru.romanow.services.warranty.model.WarrantyResponse
import java.util.*

@Service
class OrderManagementServiceImpl(
    private val orderService: OrderService,
    private val warehouseClient: WarehouseClient,
    private val warrantyClient: WarrantyClient
) : OrderManagementService {

    override fun orders(userId: String): List<OrderResponse<ItemInfo>> =
        orderService.orders(userId)
            .map {
                OrderResponse(
                    orderUid = it.uid,
                    userId = userId,
                    status = it.status,
                    orderDate = it.createdDate,
                    items = it.items?.map { i -> ItemInfo(i.name, i.count) }
                )
            }


    override fun orderByUid(userId: String, orderUid: UUID): OrderResponse<DetailedItemInfo> {
        val order = orderService.orderByUid(userId, orderUid)
        val names = order.items!!.map { it.name!! }
        val items = warehouseClient.items(names).orElse(listOf()).associateBy { it.name }
        val warranties = warrantyClient.warrantyStatus(orderUid).orElse(listOf()).associateBy { it.name }
        return OrderResponse(
            orderUid = order.uid,
            userId = userId,
            status = order.status,
            orderDate = order.createdDate,
            items = order.items?.map { i ->
                val item = items[i.name]
                DetailedItemInfo(
                    name = i.name,
                    count = i.count,
                    manufacturer = item?.manufacturer,
                    description = item?.description,
                    imageUrl = item?.imageUrl,
                    warranty = warranties[i.name]
                )
            }
        )
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
