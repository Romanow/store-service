/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service.impl

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Example
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.romanow.services.store.domain.Order
import ru.romanow.services.store.domain.OrderItem
import ru.romanow.services.store.model.OrderStatus
import ru.romanow.services.store.repository.OrderRepository
import ru.romanow.services.store.service.OrderService
import java.util.*

@Service
internal class OrderServiceImpl(private val orderRepository: OrderRepository) : OrderService {

    @Transactional(readOnly = true)
    override fun orders(userId: String) = orderRepository.findByCreatedUser(userId)

    @Transactional(readOnly = true)
    override fun orderByUid(orderUid: UUID): Order =
        orderRepository.findOne(Example.of(Order(uid = orderUid)))
            .orElseThrow { EntityNotFoundException("Order '$orderUid' not found") }

    @Transactional
    override fun create(userId: String, items: List<String>): Order {
        val order = Order(uid = UUID.randomUUID(), status = OrderStatus.PROCESSED)
        order.items = items.map { OrderItem(order = order, name = it) }
        return orderRepository.save(order)
    }
}
