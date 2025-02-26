package ru.romanow.services.warehouse

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class WarehouseApplication

fun main(args: Array<String>) {
    SpringApplication.run(WarehouseApplication::class.java, *args)
}
