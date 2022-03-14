package ru.romanow.inst.services.store.model

import javax.validation.constraints.NotEmpty

data class PurchaseRequest(
    @field:NotEmpty(message = "{field.is.empty}")
    var model: String,
    val size: SizeChart
)