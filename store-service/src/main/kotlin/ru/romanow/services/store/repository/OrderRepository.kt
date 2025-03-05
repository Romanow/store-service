/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import ru.romanow.services.store.domain.Order
import java.util.*

interface OrderRepository : JpaRepository<Order, Int> {

    @EntityGraph(attributePaths = ["items"])
    fun findByUserId(userId: String): List<Order>

    @EntityGraph(attributePaths = ["items"])
    fun findByUserIdAndUid(userId: String, orderUid: UUID): Order
}
