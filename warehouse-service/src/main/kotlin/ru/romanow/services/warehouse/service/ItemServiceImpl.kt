/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.services.warehouse.domain.Items
import ru.romanow.services.warehouse.model.ItemRequest
import ru.romanow.services.warehouse.model.ItemResponse
import ru.romanow.services.warehouse.repository.ItemRepository
import java.util.*

@Service
class ItemServiceImpl(
    private val itemRepository: ItemRepository
) : ItemService {

    @Transactional(readOnly = true)
    override fun items() = itemRepository
        .findAvailableItems()
        .map { buildItemResponse(it) }

    @Transactional(readOnly = true)
    override fun items(names: List<String>): List<ItemResponse> {
        val items = itemRepository.findItemByNames(names)
        if (items.size != names.size) {
            val itemsNotFound = names.subtract(items.map { it.name }.toSet())
            throw EntityNotFoundException("Not found information about items '$itemsNotFound!)}'")
        }
        return items.map { buildItemResponse(it) }
    }

    @Transactional
    override fun takeItems(orderUid: UUID, request: List<ItemRequest>) {
        // val items = itemRepository.findItemByNames(request.map { it.name!! })
    }

    @Transactional
    override fun returnItems(orderUid: UUID) {
        TODO("Not yet implemented")
    }

    private fun buildItemResponse(item: Items) = ItemResponse(
        name = item.name,
        description = item.description,
        manufacturer = item.manufacturer,
        availableCount = item.availableCount,
        imageUrl = item.imageUrl
    )
}
