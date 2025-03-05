/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.romanow.services.store.model.OrderStatus
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener::class)
data class Order(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "uid", nullable = false, unique = true)
    var uid: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OrderStatus? = null,

    @Column(name = "user_id", nullable = false)
    var userId: String? = null,

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    var items: List<OrderItem>? = null,

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

        other as Order

        return uid == other.uid
    }

    override fun hashCode(): Int {
        return uid?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Order(id=$id, uid=$uid, status=$status, userId=$userId, " +
            "createdDate=$createdDate, modifiedDate=$modifiedDate, modifiedUser=$modifiedUser)"
    }
}
