package ru.romanow.inst.services.store.service

import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.inst.services.common.utils.buildEx
import ru.romanow.inst.services.store.exceptions.ItemNotAvailableException
import ru.romanow.inst.services.store.exceptions.OrderProcessException
import ru.romanow.inst.services.store.model.*
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class OrderServiceImpl(
    private val orderWebClient: WebClient
) : OrderService {

    override fun getOrderInfo(userUid: UUID, orderUid: UUID): Optional<OrderInfoResponse> {
        return orderWebClient
            .get()
            .uri("/{userUid}/{orderUid}", userUid, orderUid)
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { OrderProcessException(it) } })
            .bodyToMono(OrderInfoResponse::class.java)
            .blockOptional()
    }

    override fun getOrderInfoByUser(userUid: UUID): Optional<OrdersInfoResponse> {
        return orderWebClient
            .get()
            .uri("/{userUid}", userUid)
            .retrieve()
            .onStatus({ it.isError }, { response -> buildEx(response) { OrderProcessException(it) } })
            .bodyToMono(OrdersInfoResponse::class.java)
            .blockOptional()
    }

    override fun makePurchase(userUid: UUID, request: PurchaseRequest): Optional<CreateOrderResponse> {
        return orderWebClient
            .post()
            .uri("/{userUid}", userUid)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({ it == CONFLICT }, { response -> buildEx(response) { ItemNotAvailableException(it) } })
            .onStatus({ it == UNPROCESSABLE_ENTITY }, { response -> buildEx(response) { OrderProcessException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { OrderProcessException(it) } })
            .bodyToMono(CreateOrderResponse::class.java)
            .blockOptional()
    }

    override fun refundPurchase(orderUid: UUID) {
        orderWebClient
            .delete()
            .uri("/{orderUid}", orderUid)
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { OrderProcessException(it) } })
            .toBodilessEntity()
            .block()
    }

    override fun warrantyRequest(orderUid: UUID, request: WarrantyRequest): Optional<OrderWarrantyResponse> {
        return orderWebClient
            .post()
            .uri("/{orderUid}/warranty", orderUid)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it == UNPROCESSABLE_ENTITY }, { response -> buildEx(response) { OrderProcessException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { OrderProcessException(it) } })
            .bodyToMono(OrderWarrantyResponse::class.java)
            .blockOptional()
    }
}