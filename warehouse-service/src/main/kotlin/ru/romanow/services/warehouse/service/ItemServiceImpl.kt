/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.services.warehouse.model.ItemRequest
import ru.romanow.services.warehouse.model.ItemResponse
import ru.romanow.services.warehouse.repository.ItemRepository
import java.util.*

@Service
class ItemServiceImpl(
    private val itemRepository: ItemRepository
) : ItemService {

    @Transactional(readOnly = true)
    override fun items() = itemRepository.findAvailableItems()
        .map {
            ItemResponse(
                name = it.name,
                description = it.description,
                manufacturer = it.manufacturer,
                availableCount = it.availableCount,
                imageUrl = it.imageUrl
            )
        }

    @Transactional(readOnly = true)
    override fun items(names: List<String>): Map<String, ItemResponse> {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun takeItems(orderUid: UUID, request: List<ItemRequest>): ItemResponse {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun returnItems(orderUid: UUID) {
        TODO("Not yet implemented")
    }
}
