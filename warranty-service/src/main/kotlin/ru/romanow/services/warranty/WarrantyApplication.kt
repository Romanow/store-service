/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class WarrantyApplication

fun main(args: Array<String>) {
    SpringApplication.run(WarrantyApplication::class.java, *args)
}
