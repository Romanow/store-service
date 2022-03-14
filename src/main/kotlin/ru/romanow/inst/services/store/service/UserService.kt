package ru.romanow.inst.services.store.service

import ru.romanow.inst.services.store.domain.User
import java.util.*

interface UserService {
    fun getUserById(userUid: UUID): User
    fun checkUserExists(userUid: UUID): Boolean
}