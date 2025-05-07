/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.services.store.repository.OrderRepository
import ru.romanow.services.store.service.OrderService
import java.util.*

@Service
internal class OrderServiceImpl(private val orderRepository: OrderRepository) : OrderService {

    @Transactional(readOnly = true)
    override fun orders(userId: String) = orderRepository.findByUserId(userId)

    @Transactional(readOnly = true)
    override fun orderByUid(userId: String, orderUid: UUID) =
        orderRepository.findByUserIdAndUid(userId, orderUid)
}
