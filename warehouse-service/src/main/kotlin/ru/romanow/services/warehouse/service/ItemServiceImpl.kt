/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.services.warehouse.model.ItemResponse
import ru.romanow.services.warehouse.repository.ItemRepository

@Service
class ItemServiceImpl(
    private val itemRepository: ItemRepository
) : ItemService {

    @Transactional(readOnly = true)
    override fun items() = itemRepository.findAvailableItems()
        .map {
            ItemResponse(
                uid = it.uid,
                name = it.name,
                description = it.description,
                manufacturer = it.manufacturer,
                availableCount = it.availableCount,
                imageUrl = it.imageUrl
            )
        }
}
