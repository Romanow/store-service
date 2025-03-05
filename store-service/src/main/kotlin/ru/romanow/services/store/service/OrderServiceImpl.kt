/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.service

import org.springframework.stereotype.Service
import ru.romanow.services.store.repository.OrderItemRepository
import ru.romanow.services.store.repository.OrderRepository

@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository
) : OrderService
