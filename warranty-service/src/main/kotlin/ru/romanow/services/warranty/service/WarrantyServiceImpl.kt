/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Example.of
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.services.warranty.domain.Warranty
import ru.romanow.services.warranty.exceptions.ItemNotOnWarrantyException
import ru.romanow.services.warranty.model.*
import ru.romanow.services.warranty.repository.WarrantyRepository
import java.util.*

@Service
class WarrantyServiceImpl(
    private val warrantyRepository: WarrantyRepository,
) : WarrantyService {

    @Transactional(readOnly = true)
    override fun warrantyStatus(orderUid: UUID) =
        warrantyRepository.findAll(of(Warranty(orderUid = orderUid)))
            .map {
                WarrantyStatusResponse(
                    name = it.name,
                    status = it.status,
                    comment = it.comment,
                    warrantyStartDate = it.createdDate,
                    lastUpdateDate = it.modifiedDate
                )
            }

    override fun start(orderUid: UUID, items: List<WarrantyItemInfo>) {
        val warranties = items.map { Warranty(orderUid = orderUid, name = it, status = WarrantyStatus.ON_WARRANTY) }
        warrantyRepository.saveAll(warranties)
    }

    @Transactional
    override fun warrantyRequest(orderUid: UUID, items: List<WarrantyItemInfo>): List<WarrantyResponse> {
        val warranties = warrantyRepository.findAll(of(Warranty(orderUid = orderUid)))
        val names = warranties.map { it.name!! }
        if (!request.map { it.name }.all { names.contains(it) }) {
            val itemsNotOnWarranty = request.map { it.name }.subtract(names.toSet())
            throw ItemNotOnWarrantyException("Items '$itemsNotOnWarranty' not on warranty for order '$orderUid'")
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
