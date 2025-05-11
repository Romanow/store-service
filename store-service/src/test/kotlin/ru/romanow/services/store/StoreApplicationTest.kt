/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.hamcrest.Matchers.matchesPattern
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity.accepted
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
import ru.romanow.services.warehouse.models.ItemInfo
import ru.romanow.services.warranty.models.WarrantyResponse
import ru.romanow.services.warranty.models.WarrantyStatus.ON_WARRANTY
import ru.romanow.services.warranty.models.WarrantyStatus.TAKE_NEW
import ru.romanow.services.warranty.models.WarrantyStatusResponse
import java.time.LocalDateTime.now
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
        mockMvc.get("/api/protected/v1/orders")
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
        val warrantyResponse = objectMapper.writeValueAsString(
            listOf(
                WarrantyStatusResponse(
                    name = ITEM1_NAME,
                    status = ON_WARRANTY,
                    warrantyStartDate = now(),
                    lastUpdateDate = now()
                )
            )
        )
        stubFor(
            get(urlPathEqualTo("/api/protected/v1/warranty/$ORDER_UID"))
                .willReturn(ok().withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).withBody(warrantyResponse))
        )

        val warehouseResponse = objectMapper.writeValueAsString(listOf(ItemInfo(name = ITEM1_NAME, count = 1)))
        stubFor(
            get(urlPathEqualTo("/api/protected/v1/items"))
                .withQueryParam("names", havingExactly(ITEM1_NAME))
                .willReturn(ok().withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).withBody(warehouseResponse))
        )

        mockMvc.get("/api/protected/v1/orders/$ORDER_UID")
            .andExpect {
                status { isOk() }
                status { contentType(APPLICATION_JSON) }
                content {
                    jsonPath("$.orderUid") { value(ORDER_UID.toString()) }
                    jsonPath("$.userId") { value("system") }
                    jsonPath("$.status") { value(PROCESSED.name) }
                    jsonPath("$.orderDate") { matchesPattern(ISO_DATE_TIME.toString()) }
                    jsonPath("$.items.length()") { value(1) }
                    jsonPath("$.items[0].name") { value(ITEM1_NAME) }
                    jsonPath("$.items[0].description") { isEmpty() }
                    jsonPath("$.items[0].manufacturer") { isEmpty() }
                    jsonPath("$.items[0].imageUrl") { isEmpty() }
                    jsonPath("$.items[0].warranty.status") { value(ON_WARRANTY.name) }
                    jsonPath("$.items[0].warranty.comment") { isEmpty() }
                    jsonPath("$.items[0].warranty.warrantyStartDate") { matchesPattern(ISO_DATE_TIME.toString()) }
                    jsonPath("$.items[0].warranty.lastUpdateDate") { matchesPattern(ISO_DATE_TIME.toString()) }
                }
            }
    }

    @Test
    fun `when purchase then success`() {
        val request = objectMapper.writeValueAsString(listOf(ITEM2_NAME))
        stubFor(
            post(urlMatching("/api/protected/v1/warranty/$UUID_REGEX/start"))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(equalTo(request))
                .willReturn(aResponse().withStatus(202))
        )
        stubFor(
            post(urlPathEqualTo("/api/protected/v1/items/take"))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(equalTo(request))
                .willReturn(aResponse().withStatus(202))
        )

        mockMvc.post("/api/protected/v1/orders/purchase") {
            contentType = APPLICATION_JSON
            content = objectMapper.writeValueAsString(listOf(ITEM2_NAME))
        }
            .andExpect {
                status { isCreated() }
                status {
                    header {
                        string(LOCATION, matchesPattern("http://localhost/api/protected/v1/orders/$UUID_REGEX"))
                    }
                }
            }
    }

    @Test
    fun `when warrantyRequest then success`() {
        val warrantyRequest = objectMapper.writeValueAsString(listOf(ITEM1_NAME))
        val warrantyResponse = objectMapper.writeValueAsString(
            listOf(WarrantyResponse(name = ITEM1_NAME, status = TAKE_NEW, comment = "Take new item from Warehouse"))
        )
        stubFor(
            post(urlPathEqualTo("/api/protected/v1/warranty/$ORDER_UID/request"))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(equalTo(warrantyRequest))
                .willReturn(ok().withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).withBody(warrantyResponse))
        )

        mockMvc.post("/api/protected/v1/orders/$ORDER_UID/warranty") {
            contentType = APPLICATION_JSON
            content = objectMapper.writeValueAsString(listOf(ITEM1_NAME))
        }
            .andExpect {
                status { isOk() }
                status { contentType(APPLICATION_JSON) }
                jsonPath("$.length()") { value(1) }
                jsonPath("$[0].name") { value(ITEM1_NAME) }
                jsonPath("$[0].status") { value(TAKE_NEW.name) }
                jsonPath("$[0].comment") { value("Take new item from Warehouse") }
            }
    }

    @Test
    fun `when cancel then success`() {
        val warehouseRequest = objectMapper.writeValueAsString(listOf(ITEM1_NAME))
        stubFor(
            delete(urlPathEqualTo("/api/protected/v1/warranty/$ORDER_UID/stop"))
                .willReturn(aResponse().withStatus(202))
        )
        stubFor(
            delete(urlPathEqualTo("/api/protected/v1/items/refund"))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(equalTo(warehouseRequest))
                .willReturn(aResponse().withStatus(202))
        )

        mockMvc.delete("/api/protected/v1/orders/$ORDER_UID/cancel")
            .andExpect { status { accepted() } }
    }

    companion object {
        private const val ITEM1_NAME = "Item 1"
        private const val ITEM2_NAME = "Item 2"
        private val ORDER_UID = UUID.randomUUID()
        private const val UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
    }
}
