/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.gateway.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "services")
class ServerUrlProperties : HashMap<String, String>()
