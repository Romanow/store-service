/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.common.exceptions

abstract class CircuitBreakerIgnoredException(message: String?) : RuntimeException(message)
