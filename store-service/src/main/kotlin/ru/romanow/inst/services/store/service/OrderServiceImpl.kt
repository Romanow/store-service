package ru.romanow.inst.services.store.service

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.*
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.inst.services.common.config.Fallback
import ru.romanow.inst.services.common.properties.ServerUrlProperties
import ru.romanow.inst.services.common.utils.buildEx
import ru.romanow.inst.services.order.model.CreateOrderResponse
import ru.romanow.inst.services.order.model.OrderInfoResponse
import ru.romanow.inst.services.order.model.OrdersInfoResponse
import ru.romanow.inst.services.store.exceptions.ItemNotAvailableException
import ru.romanow.inst.services.store.exceptions.OrderProcessException
import ru.romanow.inst.services.store.model.PurchaseRequest
import ru.romanow.inst.services.store.model.WarrantyRequest
import ru.romanow.inst.services.warranty.model.OrderWarrantyResponse
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class OrderServiceImpl(
    private val fallback: Fallback,
    private val orderWebClient: WebClient,
    private val properties: ServerUrlProperties,
    private val factory: ReactiveCircuitBreakerFactory<Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration, Resilience4JConfigBuilder>
) : OrderService {

    override fun getOrderInfo(userId: String, orderUid: UUID): Optional<OrderInfoResponse> {
        return orderWebClient
            .get()
            .uri("/{userId}/{orderUid}", userId, orderUid)
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { OrderProcessException(it) } })
            .bodyToMono(OrderInfoResponse::class.java)
            .transform {
                factory.create("getOrderInfo")
                    .run(it) { throwable ->
                        fallback.apply(GET, "${properties.orderUrl}/api/v1/orders/$userUid/$orderUid", throwable)
                    }
            }
            .blockOptional()
    }

    override fun getOrderInfoByUser(userId: String): Optional<OrdersInfoResponse> {
        return orderWebClient
            .get()
            .uri("/{userId}", userId)
            .retrieve()
            .onStatus({ it.isError }, { response -> buildEx(response) { OrderProcessException(it) } })
            .bodyToMono(OrdersInfoResponse::class.java)
            .transform {
                factory.create("getOrderInfoByUser")
                    .run(it) { throwable ->
                        fallback.apply(GET, "${properties.orderUrl}/api/v1/orders/$userUid", throwable)
                    }
            }
            .blockOptional()
    }

    override fun makePurchase(userId: String, request: PurchaseRequest): Optional<CreateOrderResponse> {
        return orderWebClient
            .post()
            .uri("/{userId}", userId)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({ it == CONFLICT }, { response -> buildEx(response) { ItemNotAvailableException(it) } })
            .onStatus({ it == UNPROCESSABLE_ENTITY }, { response -> buildEx(response) { OrderProcessException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { OrderProcessException(it) } })
            .bodyToMono(CreateOrderResponse::class.java)
            .transform {
                factory.create("makePurchase")
                    .run(it) { throwable ->
                        fallback.apply(POST, "${properties.orderUrl}/api/v1/orders/$userUid", throwable, request)
                    }
            }
            .blockOptional()
    }

    override fun refundPurchase(orderUid: UUID) {
        webClient
            .delete()
            .uri("/{orderUid}", orderUid)
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { OrderProcessException(it) } })
            .toBodilessEntity()
            .transform {
                factory.create("refundPurchase")
                    .run(it) { throwable ->
                        fallback.apply(DELETE, "${properties.orderUrl}/api/v1/orders/$orderUid", throwable)
                    }
            }
            .block()
    }

    override fun warrantyRequest(orderUid: UUID, request: WarrantyRequest): Optional<OrderWarrantyResponse> {
        return webClient
            .post()
            .uri("/{orderUid}/warranty", orderUid)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it == UNPROCESSABLE_ENTITY }, { response -> buildEx(response) { OrderProcessException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { OrderProcessException(it) } })
            .bodyToMono(OrderWarrantyResponse::class.java)
            .transform {
                factory.create("warrantyRequest")
                    .run(it) { throwable ->
                        fallback.apply(POST, "${properties.orderUrl}/api/v1/orders/$orderUid/warranty", throwable, request)
                    }
            }
            .blockOptional()
    }
}