/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.romanow.services.warranty.domain.Warranty

interface WarrantyRepository : JpaRepository<Warranty, Int>
