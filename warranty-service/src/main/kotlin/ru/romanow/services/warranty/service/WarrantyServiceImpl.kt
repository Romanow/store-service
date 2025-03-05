/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.service

import org.springframework.data.domain.Example
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.services.warranty.domain.Warranty
import ru.romanow.services.warranty.model.WarrantyRequest
import ru.romanow.services.warranty.model.WarrantyResponse
import ru.romanow.services.warranty.model.WarrantyStatusResponse
import ru.romanow.services.warranty.repository.WarrantyRepository
import java.util.*

@Service
class WarrantyServiceImpl(
    private val warrantyRepository: WarrantyRepository
) : WarrantyService {

    @Transactional(readOnly = true)
    override fun warrantyStatus(orderUid: UUID) =
        warrantyRepository.findAll(Example.of(Warranty(orderUid = orderUid)))
            .map {
                WarrantyStatusResponse(
                    name = it.name,
                    status = it.status,
                    comment = it.comment,
                    warrantyStartDate = it.createdDate,
                    lastUpdateDate = it.modifiedDate
                )
            }

    @Transactional
    override fun start(orderUid: UUID, names: List<String>) {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun warrantyRequest(orderUid: UUID, request: WarrantyRequest): WarrantyResponse {
        TODO("Not yet implemented")
    }

    @Transactional
    override fun stop(orderUid: UUID) {
        TODO("Not yet implemented")
    }
}
