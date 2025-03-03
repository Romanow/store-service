/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.romanow.services.warranty.model.WarrantyStatus
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "warranty")
@EntityListeners(AuditingEntityListener::class)
data class Warranty(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "order_uid", nullable = false)
    var orderUid: UUID? = null,

    @Column(name = "item_uid", nullable = false)
    var itemUid: UUID? = null,

    @Column(name = "comment")
    var comment: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: WarrantyStatus? = null,

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    var createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    var modifiedDate: LocalDateTime? = null,

    @LastModifiedBy
    @Column(name = "modified_user", nullable = false)
    var modifiedUser: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Warranty

        if (orderUid != other.orderUid) return false
        if (itemUid != other.itemUid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = orderUid?.hashCode() ?: 0
        result = 31 * result + (itemUid?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Warranty(id=$id, orderUid=$orderUid, itemUid=$itemUid, comment=$comment, status=$status, " +
            "createdDate=$createdDate, modifiedDate=$modifiedDate, modifiedUser=$modifiedUser)"
    }
}
