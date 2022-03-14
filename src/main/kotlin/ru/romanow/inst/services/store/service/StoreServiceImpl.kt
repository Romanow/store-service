package ru.romanow.inst.services.store.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.romanow.inst.services.store.exceptions.OrderProcessException
import ru.romanow.inst.services.store.exceptions.WarrantyProcessException
import ru.romanow.inst.services.store.model.*
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class StoreServiceImpl(
    private val userService: UserService,
    private val warehouseService: WarehouseService,
    private val orderService: OrderService,
    private val warrantyService: WarrantyService
) : StoreService {
    private val logger = LoggerFactory.getLogger(StoreServiceImpl::class.java)

    override fun findUserOrders(userUid: UUID): UserOrdersResponse {
        if (!userService.checkUserExists(userUid)) {
            throw EntityNotFoundException("User '$userUid' not found")
        }

        val orders: MutableList<UserOrderResponse> = ArrayList()
        logger.info("Request to Order Service for user '{}' orders", userUid)
        val userOrders = orderService.getOrderInfoByUser(userUid)

        if (userOrders.isPresent) {
            logger.info("User '$userUid' has ${userOrders.get().size} orders")

            // TODO переделать на batch-операции
            for (orderInfo in userOrders.get()) {
                val orderUid = orderInfo.orderUid
                val itemUid = orderInfo.itemUid

                logger.info("Processing user '$userUid' order '$orderUid'")
                val order = UserOrderResponse(
                    orderUid = orderUid,
                    date = orderInfo.orderDate
                )

                logger.info("Request to Warehouse for item '$itemUid' info by order '$orderUid'")
                warehouseService.getItemInfo(itemUid)
                    .ifPresent {
                        order.model = it.model
                        order.size = SizeChart.valueOf(it.size)
                    }

                logger.info("Request to Warranty for item '$itemUid' info by order '$orderUid'")
                warrantyService.getItemWarrantyInfo(itemUid)
                    .ifPresent {
                        order.warrantyDate = it.warrantyDate
                        order.warrantyStatus = WarrantyStatus.valueOf(it.status)
                    }
                orders.add(order)
            }
        } else {
            logger.warn("User '$userUid' has no orders", userUid)
        }

        return UserOrdersResponse(orders)
    }

    override fun findUserOrder(userUid: UUID, orderUid: UUID): UserOrderResponse {
        if (!userService.checkUserExists(userUid)) {
            throw EntityNotFoundException("User '$userUid' not found")
        }

        logger.info("Request to Order Service for user '$userUid' order '$orderUid'")
        val orderInfo = orderService.getOrderInfo(userUid, orderUid)
            .orElseThrow { EntityNotFoundException("Order '$orderUid' not found for user '$userUid'") }

        val orderResponse = UserOrderResponse(
            orderUid = orderUid,
            date = orderInfo.orderDate
        )

        logger.info("Processing user '$userUid' order '$orderUid'")

        val itemUid = orderInfo.itemUid

        logger.info("Request to Warehouse for item '$itemUid' info by order '$orderUid'")
        warehouseService.getItemInfo(itemUid)
            .ifPresent {
                orderResponse.model = it.model
                orderResponse.size = SizeChart.valueOf(it.size)
            }

        logger.info("Request to Warranty for item '$itemUid' info by order '$orderUid'")
        warrantyService.getItemWarrantyInfo(itemUid)
            .ifPresent {
                orderResponse.warrantyDate = it.warrantyDate
                orderResponse.warrantyStatus = WarrantyStatus.valueOf(it.status)
            }

        return orderResponse
    }

    override fun makePurchase(userUid: UUID, request: PurchaseRequest): UUID {
        logger.info("Process user '$userUid' purchase request (model: ${request.model}, size: ${request.size})")
        if (!userService.checkUserExists(userUid)) {
            throw EntityNotFoundException("User '$userUid' not found")
        }

        logger.info("Request to Order Service for user '$userUid' to process order")
        return orderService
            .makePurchase(userUid, request)
            .map { it.orderUid }
            .orElseThrow { OrderProcessException("User '$userUid' order not created") }
    }

    override fun refundPurchase(userUid: UUID, orderUid: UUID) {
        logger.info("Process user '{}' return request for order '{}'", userUid, orderUid)
        if (!userService.checkUserExists(userUid)) {
            throw EntityNotFoundException("User '$userUid' not found")
        }

        logger.info("Request to Order Service for user '$userUid' to cancel order '$orderUid'")
        orderService.refundPurchase(orderUid)
    }

    override fun warrantyRequest(userUid: UUID, orderUid: UUID, request: WarrantyRequest): WarrantyResponse {
        logger.info("Process user '$userUid' warranty request for order '$orderUid'")
        if (!userService.checkUserExists(userUid)) {
            throw EntityNotFoundException("User '$userUid' not found")
        }

        logger.info("Request to Order Service for user '$userUid' and order '$orderUid' to make warranty request (${request.reason}})")
        return orderService
            .warrantyRequest(orderUid, request)
            .map { buildWarrantyResponse(orderUid, it) }
            .orElseThrow { WarrantyProcessException("Warranty processed with exception") }
    }

    private fun buildWarrantyResponse(orderUid: UUID, response: OrderWarrantyResponse) =
        WarrantyResponse(
            orderUid = orderUid,
            warrantyDate = response.warrantyDate,
            decision = response.decision
        )
}
