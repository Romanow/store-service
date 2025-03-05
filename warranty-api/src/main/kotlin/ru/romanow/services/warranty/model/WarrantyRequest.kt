/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.model

import jakarta.validation.constraints.NotEmpty

data class WarrantyRequest(
    @field:NotEmpty(message = "{field.is.empty}")
    val name: String? = null,
    val comment: String? = null
)
