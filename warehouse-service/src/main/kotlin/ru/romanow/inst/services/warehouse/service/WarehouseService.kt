package ru.romanow.inst.services.warehouse.service

import ru.romanow.inst.services.warehouse.domain.Item
import ru.romanow.inst.services.warehouse.model.ItemInfoResponse
import ru.romanow.inst.services.warehouse.model.ItemResponse
import ru.romanow.inst.services.warehouse.model.OrderItemRequest
import ru.romanow.inst.services.warehouse.model.OrderItemResponse
import java.util.UUID

interface WarehouseService {
    fun items(): List<ItemResponse>
    fun getItemInfo(orderItemUid: UUID): ItemInfoResponse
    fun getOrderItem(orderItemUid: UUID): Item
    fun takeItem(request: OrderItemRequest): OrderItemResponse
    fun returnItem(orderItemUid: UUID)
    fun checkItemAvailableCount(orderItemUid: UUID): Int
}
