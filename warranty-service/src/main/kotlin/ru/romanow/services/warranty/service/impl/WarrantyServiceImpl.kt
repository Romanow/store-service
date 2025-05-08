/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Example.of
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.services.warranty.domain.Warranty
import ru.romanow.services.warranty.exceptions.ItemNotOnWarrantyException
import ru.romanow.services.warranty.model.WarrantyResponse
import ru.romanow.services.warranty.model.WarrantyStatus
import ru.romanow.services.warranty.model.WarrantyStatusResponse
import ru.romanow.services.warranty.repository.WarrantyRepository
import ru.romanow.services.warranty.service.WarehouseClient
import ru.romanow.services.warranty.service.WarrantyService
import java.util.*

@Service
internal class WarrantyServiceImpl(
    private val warrantyRepository: WarrantyRepository,
    private val warehouseClient: WarehouseClient
) : WarrantyService {

    @Transactional(readOnly = true)
    override fun status(orderUid: UUID) =
        warrantyRepository.findAll(of(Warranty(orderUid = orderUid)))
            .map {
                WarrantyStatusResponse(
                    name = it.name!!,
                    status = it.status!!,
                    comment = it.comment,
                    warrantyStartDate = it.createdDate!!,
                    lastUpdateDate = it.modifiedDate!!
                )
            }

    @Transactional
    override fun start(orderUid: UUID, items: List<String>) {
        val warranties = items.map {
            Warranty(
                orderUid = orderUid,
                name = it,
                status = WarrantyStatus.ON_WARRANTY
            )
        }
        warrantyRepository.saveAll(warranties)
    }

    @Transactional
    override fun warrantyRequest(orderUid: UUID, items: List<String>): List<WarrantyResponse> {
        val warranties = warrantyRepository.findAll(of(Warranty(orderUid = orderUid)))
        val warrantyMap = warranties.associateBy { it.name!! }
        if (!items.all { warrantyMap.containsKey(it) }) {
            val itemsNotOnWarranty = items.subtract(warrantyMap.keys)
            throw ItemNotOnWarrantyException("Items '$itemsNotOnWarranty' not on warranty for order '$orderUid'")
        }
        val availableItems = warehouseClient.items(warrantyMap.keys)
        if (availableItems.isPresent) {
            return availableItems.get()
                .map {
                    val warranty = warrantyMap[it.name]!!
                    if (warranty.status == WarrantyStatus.ON_WARRANTY) {
                        if (it.count > 0) {
                            warranty.status = WarrantyStatus.TAKE_NEW
                            warranty.comment = "Take new item from Warehouse"
                        } else {
                            warranty.status = WarrantyStatus.REPAIR
                            warranty.comment = "Send to repair because Warehouse don't have enough items"
                        }
                    }
                    WarrantyResponse(
                        name = it.name,
                        status = warranty.status!!,
                        comment = warranty.comment!!
                    )
                }
        }

        return listOf()
    }

    @Transactional
    override fun stop(orderUid: UUID) {
        val updated = warrantyRepository.stop(orderUid)
        if (updated == 0) {
            throw EntityNotFoundException("Warranties for order $orderUid not found")
        }
    }
}
