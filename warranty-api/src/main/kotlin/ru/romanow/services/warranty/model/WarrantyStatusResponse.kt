/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.model

import java.time.LocalDateTime

data class WarrantyStatusResponse(
    var name: String? = null,
    var status: WarrantyStatus? = null,
    var comment: String? = null,
    var warrantyStartDate: LocalDateTime? = null,
    var lastUpdateDate: LocalDateTime? = null,
)
