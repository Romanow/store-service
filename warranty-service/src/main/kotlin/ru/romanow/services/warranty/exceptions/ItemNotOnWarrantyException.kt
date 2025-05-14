/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.exceptions

import ru.romanow.services.common.exceptions.CircuitBreakerIgnoredException

class ItemNotOnWarrantyException(message: String?) : CircuitBreakerIgnoredException(message)
