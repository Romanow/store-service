/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.romanow.services.warehouse.domain.Items

interface ItemRepository : JpaRepository<Items, Int> {
    @Query("select i from Items i where i.availableCount > 0")
    fun findAvailableItems(): List<Items>
}
