/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.romanow.services.warehouse.domain.Items

interface ItemRepository : JpaRepository<Items, Int> {
    @Query("select i from Items i where i.availableCount > 0")
    fun findAvailableItems(): List<Items>

    @Query("select i from Items i where i.name in (:names)")
    fun findItemByNames(@Param("names") names: List<String>?): List<Items>

    @Modifying
    @Query("update Items i set i.availableCount = i.availableCount - 1 where i.name in :names")
    fun returnItems(@Param("names") names: List<String>): Int
}
