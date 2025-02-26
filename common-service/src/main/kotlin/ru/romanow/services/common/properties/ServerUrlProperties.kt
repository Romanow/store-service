package ru.romanow.services.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "services")
data class ServerUrlProperties(
    val storeUrl: String? = null,
    val warehouseUrl: String? = null,
    val warrantyUrl: String? = null
)
