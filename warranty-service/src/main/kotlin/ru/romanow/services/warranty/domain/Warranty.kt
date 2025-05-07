/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
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

    @OneToMany(mappedBy = "warranty", fetch = FetchType.LAZY)
    var items: List<WarrantyItem>? = null,

    @Column(name = "active", nullable = false)
    var active: Boolean? = null,

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

        return orderUid == other.orderUid
    }

    override fun hashCode(): Int {
        return orderUid?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Warranty(id=$id, orderUid=$orderUid, active=$active, " +
            "createdDate=$createdDate, modifiedDate=$modifiedDate, modifiedUser=$modifiedUser)"
    }
}
