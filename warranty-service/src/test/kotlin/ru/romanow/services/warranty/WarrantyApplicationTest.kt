package ru.romanow.services.warranty

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import ru.romanow.services.warranty.config.DatabaseTestConfiguration

@ActiveProfiles("test")
@SpringBootTest
@Import(DatabaseTestConfiguration::class)
internal class WarrantyApplicationTest {

    @Test
    fun test() {
    }
}
