/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpHeaders.LOCATION
import org.hamcrest.Matchers.matchesPattern
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.function.RequestPredicates.contentType
import org.wiremock.spring.EnableWireMock
import ru.romanow.services.store.config.DatabaseTestConfiguration
import ru.romanow.services.store.domain.Order
import ru.romanow.services.store.domain.OrderItem
import ru.romanow.services.store.model.OrderStatus.PROCESSED
import ru.romanow.services.store.repository.OrderRepository
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
@EnableWireMock
@Transactional
@AutoConfigureTestEntityManager
@AutoConfigureMockMvc
@Import(DatabaseTestConfiguration::class)
internal class StoreApplicationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @BeforeEach
    fun beforeEach() {
        val order = Order(uid = ORDER_UID, status = PROCESSED)
        order.items = listOf(OrderItem(name = ITEM1_NAME, order = order))
        orderRepository.save(order)

    }

    @Test
    fun `when orders then success`() {
        mockMvc.get("/api/public/v1/orders")
            .andExpect {
                status { isOk() }
                status { contentType(APPLICATION_JSON) }
                content {
                    jsonPath("$.length()") { value(1) }
                    jsonPath("$[0].orderUid") { value(ORDER_UID.toString()) }
                    jsonPath("$[0].userId") { value("system") }
                    jsonPath("$[0].status") { value(PROCESSED.name) }
                    jsonPath("$[0].orderDate") { matchesPattern(ISO_DATE_TIME.toString()) }
                    jsonPath("$[0].items.length()") { value(1) }
                    jsonPath("$[0].items[0]") { value(ITEM1_NAME) }
                }
            }
    }

    @Test
    fun `when orderByUid then success`() {
        mockMvc.get("/api/public/v1/orders/$ORDER_UID")
            .andExpect {
                status { isOk() }
                status { contentType(APPLICATION_JSON) }
                content {
                    jsonPath("$.length()") { value(1) }
                    jsonPath("$[0].orderUid") { value(ORDER_UID) }
                    jsonPath("$[0].userId") { value("system") }
                    jsonPath("$[0].status") { value(PROCESSED.name) }
                    jsonPath("$[0].orderDate") { matchesPattern(ISO_DATE_TIME.toString()) }
                    jsonPath("$[0].items.length()") { value(1) }
                }
            }
    }

    @Test
    fun `when purchase then success`() {
        mockMvc.post("/api/public/v1/orders/purchase") {
            contentType(APPLICATION_JSON)
            content = objectMapper.writeValueAsString(listOf(ITEM2_NAME))
        }
            .andExpect {
                status { isCreated() }
                status {
                    header {
                        string(LOCATION, matchesPattern("/api/public/v1/orders/\\d"))
                    }
                }
            }
    }

    @Test
    fun `when warranty then success`() {
        mockMvc.post("/api/public/v1/orders/$ORDER_UID/warranty") {
            contentType(APPLICATION_JSON)
        }
            .andExpect {
                status { isOk() }
                status { contentType(APPLICATION_JSON) }
            }
    }

    @Test
    fun `when cancel then success`() {
        mockMvc.delete("/api/public/v1/orders/$ORDER_UID/cancel")
            .andExpect { status { isNoContent() } }
    }


    companion object {
        private const val ITEM1_NAME = "Item 1"
        private const val ITEM2_NAME = "Item 2"
        private val ORDER_UID = UUID.randomUUID()
    }
}
