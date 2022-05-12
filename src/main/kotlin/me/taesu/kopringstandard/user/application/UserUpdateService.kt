package me.taesu.kopringstandard.user.application

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
        with(request) {
            user.update(name = name, birthDate = birthDate)
        }
    }
}

class UserUpdateRequest(
    val name: String,
    var birthDate: LocalDate,
)