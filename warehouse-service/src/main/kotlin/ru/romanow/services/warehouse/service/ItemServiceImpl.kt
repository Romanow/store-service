/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.service

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.services.warehouse.exceptions.ItemNotAvailableException
import ru.romanow.services.warehouse.model.ItemInfo
import ru.romanow.services.warehouse.repository.ItemRepository

@Service
class ItemServiceImpl(
    private val itemRepository: ItemRepository
) : ItemService {
    private val logger = LoggerFactory.getLogger(ItemServiceImpl::class.java)

    @Transactional(readOnly = true)
    override fun availableItems() = itemRepository
        .findAvailableItems()
        .map {
            ItemInfo(
                name = it.name!!,
                count = it.availableCount,
                description = it.description,
                manufacturer = it.manufacturer,
                imageUrl = it.imageUrl
            )
        }

    @Transactional(readOnly = true)
    override fun items(names: List<String>): List<ItemInfo> {
        val items = itemRepository.findItemByNames(names)
        if (items.size != names.size) {
            val itemsNotFound = names.subtract(items.map { it.name }.toSet())
            throw EntityNotFoundException("Not found information about items '$itemsNotFound')}'")
        }
        return items.map {
            ItemInfo(
                name = it.name!!,
                count = it.availableCount,
                description = it.description,
                manufacturer = it.manufacturer,
                imageUrl = it.imageUrl
            )
        }
    }

    @Transactional
    override fun takeItems(names: List<String>) {
        val items = itemRepository.findItemByNames(names)
        if (items.size != names.size) {
            val itemsNotFound = names.subtract(items.map { it.name }.toSet())
            throw EntityNotFoundException("Not found information about items '$itemsNotFound!)}'")
        }
        val itemsNotAvailable = items.filter { it.availableCount < 1 }.map { it.name }
        if (itemsNotAvailable.isNotEmpty()) {
            throw ItemNotAvailableException("Not available items '$itemsNotAvailable'")
        }
        items.forEach { it.availableCount -= 1 }
        logger.info("Take ${items.size} items: '$names'")
    }

    @Transactional
    override fun returnItems(names: List<String>) {
        val updated = itemRepository.returnItems(names)
        logger.info("Returned $updated items: '$names'")
    }
}
