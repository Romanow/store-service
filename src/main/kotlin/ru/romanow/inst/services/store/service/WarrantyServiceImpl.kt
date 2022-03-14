package ru.romanow.inst.services.store.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import ru.romanow.inst.services.common.utils.buildEx
import ru.romanow.inst.services.store.exceptions.WarrantyProcessException
import ru.romanow.inst.services.store.model.WarrantyInfoResponse
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class WarrantyServiceImpl(
    private val warrantyWebClient: WebClient
) : WarrantyService {

    override fun getItemWarrantyInfo(itemUid: UUID): Optional<WarrantyInfoResponse> {
        return warrantyWebClient
            .get()
            .uri("/{itemUid}", itemUid)
            .retrieve()
            .onStatus({ it == HttpStatus.NOT_FOUND }, { response -> buildEx(response) { EntityNotFoundException(it) } })
            .onStatus({ it.isError }, { response -> buildEx(response) { WarrantyProcessException(it) } })
            .bodyToMono(WarrantyInfoResponse::class.java)
            .blockOptional()
    }
}