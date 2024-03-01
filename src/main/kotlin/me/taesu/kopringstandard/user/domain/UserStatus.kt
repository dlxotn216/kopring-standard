package me.taesu.kopringstandard.user.domain

import me.taesu.kopringstandard.app.domain.RawCode

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
enum class UserStatus(override val code: String): RawCode {
    ACTIVE("A"),
    INACTIVE("I"),
    DELETED("D")
    ;
}
