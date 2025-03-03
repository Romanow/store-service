/*
 * Copyright (c) Romanov Alexey, 2025
 */
package ru.romanow.services.warehouse.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "items")
@EntityListeners(AuditingEntityListener::class)
data class Items(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "uid", nullable = false, unique = true)
    var uid: UUID? = null,

    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "available_count", nullable = false)
    var availableCount: Int = 0,

    @Column(name = "manufacturer")
    var manufacturer: String? = null,

    @Column(name = "image_url")
    var imageUrl: String? = null,

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

        other as Items

        return uid == other.uid
    }

    override fun hashCode(): Int {
        return uid?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Items(id=$id, uid=$uid, name=$name, description=$description, availableCount=$availableCount, " +
            "createdDate=$createdDate, modifiedDate=$modifiedDate, modifiedUser=$modifiedUser)"
    }
}
