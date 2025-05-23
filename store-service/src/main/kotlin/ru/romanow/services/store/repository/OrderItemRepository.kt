/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.romanow.services.store.domain.OrderItem

interface OrderItemRepository : JpaRepository<OrderItem, Int>
