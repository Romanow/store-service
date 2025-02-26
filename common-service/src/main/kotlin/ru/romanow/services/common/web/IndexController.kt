package ru.romanow.services.common.web

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
class IndexController(
    @Value("\${spring.application.name}")
    private val applicationName: String
) {
    @GetMapping
    fun index(): String {
        return "Hello from $applicationName"
    }
}
