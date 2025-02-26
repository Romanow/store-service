package ru.romanow.services.common.config

interface CircuitBreakerConfigurationSupport {
    fun ignoredExceptions(): Array<Class<out Throwable>> {
        return arrayOf()
    }
}
