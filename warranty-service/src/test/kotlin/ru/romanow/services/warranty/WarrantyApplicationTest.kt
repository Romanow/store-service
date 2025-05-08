/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty

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
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.function.RequestPredicates.contentType
import org.wiremock.spring.EnableWireMock
import ru.romanow.services.warehouse.models.ItemInfo
import ru.romanow.services.warranty.config.DatabaseTestConfiguration
import ru.romanow.services.warranty.domain.Warranty
import ru.romanow.services.warranty.model.WarrantyStatus.ON_WARRANTY
import ru.romanow.services.warranty.model.WarrantyStatus.TAKE_NEW
import ru.romanow.services.warranty.repository.WarrantyRepository
import java.time.format.DateTimeFormatter.ISO_DATE_TIME
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
@EnableWireMock
@Transactional
@AutoConfigureTestEntityManager
@AutoConfigureMockMvc
@Import(DatabaseTestConfiguration::class)
internal class WarrantyApplicationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var warrantyRepository: WarrantyRepository

    @BeforeEach
    fun beforeEach() {
        warrantyRepository.saveAll(
            listOf(Warranty(name = ITEM1_NAME, orderUid = ORDER_UID, status = ON_WARRANTY))
        )
    }

    @Test
    fun `when status then success`() {
        mockMvc.get("/api/private/v1/warranty/$ORDER_UID")
            .andExpect {
                status { isOk() }
                status { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$.length()") { value(1) }
                    jsonPath("$[0].name") { value(ITEM1_NAME) }
                    jsonPath("$[0].status") { value(ON_WARRANTY.name) }
                    jsonPath("$[0].comment") { doesNotExist() }
                    jsonPath("$[0].warrantyStartDate") { matchesPattern(ISO_DATE_TIME.toString()) }
                    jsonPath("$[0].lastUpdateDate") { matchesPattern(ISO_DATE_TIME.toString()) }
                }
            }
    }

    @Test
    fun `when warrantyRequest then success`() {
        val responseBody = objectMapper.writeValueAsString(listOf(ItemInfo(name = ITEM1_NAME, count = 1)))
        stubFor(
            get(urlPathEqualTo("/api/private/v1/items"))
                .withQueryParam("names", havingExactly(ITEM1_NAME))
                .willReturn(ok().withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE).withBody(responseBody))
        )

        mockMvc.post("/api/private/v1/warranty/$ORDER_UID/request") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(listOf(ITEM1_NAME))
        }
            .andExpect {
                status { isOk() }
                status { contentType(MediaType.APPLICATION_JSON) }
                content {
                    jsonPath("$.length()") { value(1) }
                    jsonPath("$[0].name") { value(ITEM1_NAME) }
                    jsonPath("$[0].status") { value(TAKE_NEW.name) }
                    jsonPath("$[0].comment") { value("Take new item from Warehouse") }
                }
            }
    }

    @Test
    fun `when start then success`() {
        mockMvc.post("/api/private/v1/warranty/$ORDER_UID/start") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(listOf(ITEM2_NAME))
        }
            .andExpect { status { isAccepted() } }
    }

    @Test
    fun `when stop then success`() {
        mockMvc.delete("/api/private/v1/warranty/$ORDER_UID/stop") {
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isAccepted() } }
    }

    companion object {
        private const val ITEM1_NAME = "Item 1"
        private const val ITEM2_NAME = "Item 2"
        private val ORDER_UID = UUID.randomUUID()
    }
}
