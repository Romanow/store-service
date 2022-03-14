package ru.romanow.inst.services.store.service

import ru.romanow.inst.services.store.model.*
import java.util.*

interface StoreService {
    fun findUserOrders(userUid: UUID): UserOrdersResponse
    fun findUserOrder(userUid: UUID, orderUid: UUID): UserOrderResponse
    fun makePurchase(userUid: UUID, request: PurchaseRequest): UUID
    fun refundPurchase(userUid: UUID, orderUid: UUID)
    fun warrantyRequest(userUid: UUID, orderUid: UUID, request: WarrantyRequest): WarrantyResponse
}