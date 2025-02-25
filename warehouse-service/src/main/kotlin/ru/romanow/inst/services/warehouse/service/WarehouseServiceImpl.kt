package ru.romanow.inst.services.warehouse.service

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.inst.services.warehouse.domain.Item
import ru.romanow.inst.services.warehouse.domain.OrderItem
import ru.romanow.inst.services.warehouse.exceptions.ItemNotAvailableException
import ru.romanow.inst.services.warehouse.model.*
import ru.romanow.inst.services.warehouse.repository.ItemRepository
import ru.romanow.inst.services.warehouse.repository.OrderItemRepository
import java.util.UUID

@Service
class WarehouseServiceImpl(
    private val itemRepository: ItemRepository,
    private val orderItemRepository: OrderItemRepository
) : WarehouseService {
    private val logger = LoggerFactory.getLogger(WarehouseServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun items(): List<ItemResponse> =
        itemRepository.findAvailableItems()
            .map { ItemResponse(model = it.model!!, size = it.size!!.name, availableCount = it.availableCount) }

    @Transactional(readOnly = true)
    override fun getItemInfo(orderItemUid: UUID): ItemInfoResponse =
        itemRepository.findByOrderItemUid(orderItemUid)
            .map { buildItemInfo(it) }
            .orElseThrow { EntityNotFoundException("Item not found for orderItemUid '$orderItemUid'") }

    @Transactional(readOnly = true)
    override fun getOrderItem(orderItemUid: UUID): Item =
        itemRepository
            .findByOrderItemUid(orderItemUid)
            .orElseThrow { EntityNotFoundException("Item not found for orderItemUid '$orderItemUid'") }

    @Transactional
    override fun takeItem(request: OrderItemRequest): OrderItemResponse {
        val model = request.model
        val size = SizeChart.valueOf(request.size)
        val orderUid = request.orderUid

        logger.info("Take item (model: $model, sie: $size) for order '$orderUid'")

        val item = itemRepository.findItemByModelAndSize(model, size)
            .orElseThrow { EntityNotFoundException("Item with model '$model' and size '$size' not found") }

        if (item.availableCount < 1) {
            throw ItemNotAvailableException("Item '$item' is finished on warehouse")
        }

        val orderItemUid = UUID.randomUUID()
        var orderItem = OrderItem(
            item = item,
            orderUid = orderUid,
            orderItemUid = orderItemUid,
            canceled = false
        )
        orderItem = orderItemRepository.save(orderItem)
        logger.info("Create OrderItem '$orderItem' for order '$orderUid'")

        itemRepository.takeOneItem(item.id!!)
        logger.info("Take one item for itemId '${item.id}'")
        return buildOrderItemResponse(orderItem, item)
    }

    @Transactional
    override fun returnItem(orderItemUid: UUID) {
        itemRepository.returnOneItem(orderItemUid)
        orderItemRepository.cancelOrderItem(orderItemUid)
        logger.info("Return one item '$orderItemUid' to warehouse")
    }

    @Transactional(readOnly = true)
    override fun checkItemAvailableCount(orderItemUid: UUID) =
        getOrderItem(orderItemUid).availableCount

    private fun buildItemInfo(item: Item) = ItemInfoResponse(
        model = item.model!!,
        size = item.size!!.name
    )

    private fun buildOrderItemResponse(orderItem: OrderItem, item: Item) =
        OrderItemResponse(
            orderItemUid = orderItem.orderItemUid!!,
            orderUid = orderItem.orderUid!!,
            size = item.size!!.name,
            model = item.model!!
        )
}
