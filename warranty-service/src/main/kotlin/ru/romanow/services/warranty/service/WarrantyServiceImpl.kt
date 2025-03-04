/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.service

import jakarta.persistence.EntityNotFoundException
import org.bouncycastle.asn1.x500.style.RFC4519Style.name
import org.springframework.data.domain.Example
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.services.warranty.repository.WarrantyRepository
import ru.romanow.services.warranty.domain.Warranty
import ru.romanow.services.warranty.model.WarrantyStatusResponse
import java.util.*

@Service
class WarrantyServiceImpl(
    private val warrantyRepository: WarrantyRepository
) : WarrantyService {

    @Transactional(readOnly = true)
    override fun orderWarrantyStatus(orderUid: UUID) =
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
}
