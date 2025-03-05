/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.romanow.services.warranty.domain.Warranty
import java.util.*

interface WarrantyRepository : JpaRepository<Warranty, Int> {

    @Modifying
    @Query("update Warranty set status = 'REMOVED_FROM_WARRANTY' where orderUid = :orderUid")
    fun stop(@Param("orderUid") orderUid: UUID): Int
}
