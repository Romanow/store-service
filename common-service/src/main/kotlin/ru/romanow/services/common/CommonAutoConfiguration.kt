/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.common

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import ru.romanow.services.common.properties.ActuatorSecurityProperties
import ru.romanow.services.common.properties.CircuitBreakerConfigurationProperties
import ru.romanow.services.common.properties.ServerUrlProperties

@ComponentScan("ru.romanow.services.common")
@EnableConfigurationProperties(
    value = [
        ActuatorSecurityProperties::class,
        CircuitBreakerConfigurationProperties::class,
        ServerUrlProperties::class
    ]
)
class CommonAutoConfiguration {
    private val logger = LoggerFactory.getLogger(CommonAutoConfiguration::class.java)

    @PostConstruct
    fun postConstruct() {
        logger.info("Common configuration module loaded")
    }
}
