package me.taesu.kopringstandard.user.application

import me.taesu.kopringstandard.app.exception.UserEmailDuplicatedException
import org.springframework.stereotype.Service

@Service
class UserExceptionService {
    fun update(): String {
        throw UserEmailDuplicatedException("test")
    }
}
