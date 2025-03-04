/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.store.domain

import jakarta.persistence.*

@Entity
@Table(name = "order_items")
data class OrderItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "order_id", nullable = false, updatable = false, insertable = false)
    var orderId: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = ForeignKey(name = "fk_order_items_order_id"))
    var order: Order? = null,

    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "count", nullable = false)
    var count: Int? = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderItem

        if (orderId != other.orderId) return false
        if (name != other.name) return false
        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        var result = orderId ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (count ?: 0)
        return result
    }

    override fun toString(): String {
        return "OrderItem(id=$id, name=$name, orderId=$orderId, count=$count)"
    }
}
