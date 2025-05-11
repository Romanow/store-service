/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.function.RequestPredicates.contentType
import ru.romanow.services.warehouse.config.DatabaseTestConfiguration
import ru.romanow.services.warehouse.domain.Items
import ru.romanow.services.warehouse.repository.ItemRepository

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@AutoConfigureMockMvc
@Import(DatabaseTestConfiguration::class)
internal class WarehouseApplicationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var itemRepository: ItemRepository

    @BeforeEach
    fun beforeEach() {
        itemRepository.deleteAll()
        itemRepository.saveAll(
            listOf(
                Items(name = ITEM1_NAME, availableCount = 0),
                Items(name = ITEM2_NAME, availableCount = 1)
            )
        )
    }

    @Test
    fun `when availableItems then success`() {
        mockMvc.get("/api/public/v1/items")
            .andExpect {
                status { isOk() }
                status { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$.length()") { value(1) }
                    jsonPath("$[0].name") { value(ITEM2_NAME) }
                    jsonPath("$[0].count") { value(1) }
                }
            }
    }

    @Test
    fun `when items then success`() {
        mockMvc.get("/api/private/v1/items") {
            queryParam("names", ITEM1_NAME)
            queryParam("names", ITEM2_NAME)
        }
            .andExpect {
                status { isOk() }
                status { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$.length()") { value(2) }
                    jsonPath("$[0].name") { value(ITEM1_NAME) }
                    jsonPath("$[0].count") { value(0) }
                    jsonPath("$[1].name") { value(ITEM2_NAME) }
                    jsonPath("$[1].count") { value(1) }
                }
            }
    }

    @Test
    fun `when take then success`() {
        mockMvc.post("/api/private/v1/items/take") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(listOf(ITEM2_NAME))
        }
            .andExpect { status { isAccepted() } }
    }

    @Test
    fun `when take then ItemNotAvailableException`() {
        mockMvc.post("/api/private/v1/items/take") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(listOf(ITEM1_NAME))
        }
            .andExpect { status { isConflict() } }
    }

    @Test
    fun `when refund then success`() {
        mockMvc.delete("/api/private/v1/items/refund") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(listOf(ITEM1_NAME))
        }
            .andExpect { status { isAccepted() } }
    }

    companion object {
        private const val ITEM1_NAME = "Item 1"
        private const val ITEM2_NAME = "Item 2"
    }
}
