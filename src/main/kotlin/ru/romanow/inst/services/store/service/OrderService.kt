package ru.romanow.inst.services.store.service

import ru.romanow.inst.services.store.model.*
import java.util.*

interface OrderService {
    fun getOrderInfo(userUid: UUID, orderUid: UUID): Optional<OrderInfoResponse>
    fun getOrderInfoByUser(userUid: UUID): Optional<OrdersInfoResponse>
    fun makePurchase(userUid: UUID, request: PurchaseRequest): Optional<CreateOrderResponse>
    fun refundPurchase(orderUid: UUID)
    fun warrantyRequest(orderUid: UUID, request: WarrantyRequest): Optional<OrderWarrantyResponse>
}
