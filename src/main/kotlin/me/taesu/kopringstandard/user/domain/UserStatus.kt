package me.taesu.kopringstandard.user.domain

import me.taesu.kopringstandard.app.domain.I18nCode
import me.taesu.kopringstandard.app.domain.RawCode

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
enum class UserStatus(
    override val codeId: String = "USER_STATUS",
    override val description: String,
    override val code: String,
): I18nCode, RawCode {
    ACTIVE(description = "활성화", code = "A"),
    INACTIVE(description = "비활성화", code = "I"),
    DELETED(description = "삭제됨", code = "D")
    ;
}
