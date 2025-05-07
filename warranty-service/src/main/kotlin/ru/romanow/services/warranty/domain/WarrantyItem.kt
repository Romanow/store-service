/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warranty.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import ru.romanow.services.warranty.model.WarrantyStatus
import java.time.LocalDateTime

@Entity
@Table(name = "warranty_item")
@EntityListeners(AuditingEntityListener::class)
data class WarrantyItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "count", nullable = false)
    var count: Int? = null,

    @Column(name = "comment", length = 1024)
    var comment: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: WarrantyStatus? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warranty_id", nullable = false, foreignKey = ForeignKey(name = "fk_warranty_item_warranty_id"))
    var warranty: Warranty? = null,

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    var createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    var modifiedDate: LocalDateTime? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WarrantyItem

        if (id != other.id) return false
        if (name != other.name) return false
        if (count != other.count) return false
        if (comment != other.comment) return false
        if (status != other.status) return false
        if (createdDate != other.createdDate) return false
        if (modifiedDate != other.modifiedDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (count ?: 0)
        result = 31 * result + (comment?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (createdDate?.hashCode() ?: 0)
        result = 31 * result + (modifiedDate?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "WarrantyItem(id=$id, name=$name, count=$count, comment=$comment, status=$status, " +
            "createdDate=$createdDate, modifiedDate=$modifiedDate)"
    }
}
