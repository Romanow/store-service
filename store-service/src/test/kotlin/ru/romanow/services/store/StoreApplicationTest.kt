/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import ru.romanow.services.store.config.DatabaseTestConfiguration

@ActiveProfiles("test")
@SpringBootTest
@Import(DatabaseTestConfiguration::class)
internal class StoreApplicationTest {

    @Test
    fun test() {
    }
}
