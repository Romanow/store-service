/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.exceptions

import ru.romanow.services.common.exceptions.CircuitBreakerIgnoredException

class ItemNotAvailableException(message: String?) : CircuitBreakerIgnoredException(message)
