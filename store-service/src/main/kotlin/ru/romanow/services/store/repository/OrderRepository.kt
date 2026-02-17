/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import ru.romanow.services.store.domain.Order
import ru.romanow.services.store.model.OrderStatus
import java.util.*

interface OrderRepository : JpaRepository<Order, Int> {

    @EntityGraph(attributePaths = ["items"])
    fun findByCreatedUser(userId: String): List<Order>

    @Modifying
    @Query("UPDATE Order SET status = :status WHERE uid = :orderUid")
    fun updateStatus(orderUid: UUID, status: OrderStatus)
}
