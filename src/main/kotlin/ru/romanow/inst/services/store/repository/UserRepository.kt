package ru.romanow.inst.services.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.romanow.inst.services.store.domain.User
import java.util.*

interface UserRepository : JpaRepository<User, Int> {
    fun findByUserUid(userUid: UUID): Optional<User>
}