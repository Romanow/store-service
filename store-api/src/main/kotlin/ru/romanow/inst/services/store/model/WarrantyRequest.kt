package ru.romanow.inst.services.store.model

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class WarrantyRequest(
    @field:NotEmpty(message = "{field.is.empty")
    var reason: String
)