package me.taesu.kopringstandard.user.infra

import me.taesu.kopringstandard.user.domain.UserStatus
import me.taesu.kopringstandard.user.domain.UserType
import java.time.LocalDate

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
interface UserQuery {
    fun selectUser(criteria: UserSelectSqlCriteria): UserSelectSqlResult?
}

class UserSelectSqlCriteria(
    val userKey: Long
)
class UserSelectSqlResult(
    val userKey: Long,
    val email: String,
    val name: String,
    val birthDate: LocalDate,
    val weight: Int?,
    val nickname: String?,
    val type: UserType,
    val status: UserStatus,
)
