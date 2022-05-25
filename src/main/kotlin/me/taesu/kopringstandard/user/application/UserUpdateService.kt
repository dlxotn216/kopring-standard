package me.taesu.kopringstandard.user.application

import me.taesu.kopringstandard.app.exception.UserEmailDuplicatedException
import me.taesu.kopringstandard.app.exception.validate
import me.taesu.kopringstandard.user.domain.UserRepository
import me.taesu.kopringstandard.user.domain.findByKeyOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * Created by itaesu on 2022/05/10.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@Service
class UserUpdateService(private val userRepository: UserRepository) {
    @Transactional
    fun update(userKey: Long, request: UserUpdateRequest) {
        val user = userRepository.findByKeyOrThrow(userKey)
        val existsByUserInfoEmail = userRepository.existsByUserInfoEmail(request.email)
        validate(!(user.email != request.email && existsByUserInfoEmail)) {
            UserEmailDuplicatedException(request.email)
        }
        with(request) {
            user.update(email = email, name = name, birthDate = birthDate)
        }
    }
}

class UserUpdateRequest(
    val email: String,
    val name: String,
    val birthDate: LocalDate,
)