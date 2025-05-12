/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.services.store.exceptions.OrderAccessException
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
    private val logger = LoggerFactory.getLogger(OrderManagementServiceImpl::class.java)

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
                val details = itemDetails[it]
                val warranty = warrantyDetails[it]
                ItemInfo(
                    name = it,
                    description = details?.description,
                    manufacturer = details?.manufacturer,
                    imageUrl = details?.imageUrl,
                    warranty = WarrantyStatusInfo(
                        status = warranty?.status?.let { s -> WarrantyStatus.valueOf(s.name) },
                        comment = warranty?.comment,
                        warrantyStartDate = warranty?.warrantyStartDate,
                        lastUpdateDate = warranty?.lastUpdateDate
                    )
                )
            }
        )
    }

    override fun purchase(userId: String, items: List<String>): UUID {
        warehouseClient.take(items)
        logger.info("Took items '$items' from Warehouse Service")
        val order = orderService.create(userId, items)
        logger.info("Created order for user '$userId' with '$items'")
        warrantyClient.start(order.uid!!, items)
        logger.info("Started warranty '$items' on Warehouse Service")
        return order.uid!!
    }

    override fun warrantyRequest(userId: String, orderUid: UUID, items: List<String>): List<WarrantyResponse> {
        val order = orderService.orderByUid(orderUid)
        if (order.createdUser != userId) {
            logger.error("User '$userId' can't access order '$orderUid'")
            throw OrderAccessException("User '$userId' can't access order '$orderUid'")
        }
        logger.info("Check warranty for '$items' on order '$orderUid' on Warranty Service")
        return warrantyClient.request(orderUid, items)
            .orElse(listOf())
            .map { WarrantyResponse(it.name, WarrantyStatus.valueOf(it.status.name), it.comment) }
    }

    override fun cancel(userId: String, orderUid: UUID) {
        val order = orderService.orderByUid(orderUid)
        if (order.createdUser != userId) {
            logger.error("User '$userId' can't access order '$orderUid'")
            throw OrderAccessException("User '$userId' can't access order '$orderUid'")
        }
        order.status = OrderStatus.CANCELED
        logger.info("Canceled order '$orderUid'")
        warrantyClient.stop(orderUid)
        logger.info("Stopped warranty for order '$orderUid' on Warranty Service")
        val items = order.items!!.map { it.name!! }
        warehouseClient.refund(items)
        logger.info("Returned items '$items' on Warehouse Service")
    }
}
