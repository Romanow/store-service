package ru.romanow.inst.services.store.service

import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.inst.services.common.utils.buildEx
import ru.romanow.inst.services.store.exceptions.WarehouseProcessException
import ru.romanow.inst.services.store.model.ItemInfoResponse
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class WarehouseServiceImpl(
    private val warehouseWebClient: WebClient
) : WarehouseService {

    override fun getItemInfo(itemUid: UUID): Optional<ItemInfoResponse> {
        return warehouseWebClient
            .get()
            .uri("/{itemUid}", itemUid)
            .retrieve()
            .onStatus({ it == NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { WarehouseProcessException(it) } })
            .bodyToMono(ItemInfoResponse::class.java)
            .blockOptional()
    }
}