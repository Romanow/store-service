package ru.romanow.services.warehouse

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import ru.romanow.services.warehouse.config.DatabaseTestConfiguration

@ActiveProfiles("test")
@SpringBootTest
@Import(DatabaseTestConfiguration::class)
internal class WarehouseApplicationTest {

    @Test
    fun test() {
    }
}
