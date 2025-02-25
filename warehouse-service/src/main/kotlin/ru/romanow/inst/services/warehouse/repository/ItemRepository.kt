package ru.romanow.inst.services.warehouse.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.romanow.inst.services.warehouse.domain.Item
import ru.romanow.inst.services.warehouse.model.SizeChart
import java.util.Optional
import java.util.UUID

interface ItemRepository : JpaRepository<Item, Int> {

    @Query("select i from Item i where i.availableCount > 0")
    fun findAvailableItems(): List<Item>

    @Query("select oi.item from OrderItem oi where oi.orderItemUid = :orderItemUid")
    fun findByOrderItemUid(orderItemUid: UUID): Optional<Item>

    @Query("select i from Item i where i.model = :model and i.size = :size")
    fun findItemByModelAndSize(@Param("model") model: String, @Param("size") size: SizeChart): Optional<Item>

    @Modifying
    @Query("update Item set availableCount = availableCount - 1 where id = :id")
    fun takeOneItem(@Param("id") id: Int)

    @Modifying
    @Query(
        """
            update Item set availableCount = availableCount + 1
            where id = (select id from OrderItem oi where oi.orderItemUid = :orderItemUid)
        """
    )
    fun returnOneItem(@Param("orderItemUid") orderItemUid: UUID)
}
