package me.taesu.kopringstandard.user.interfaces

import me.taesu.kopringstandard.app.exception.throwInvalidUserType
import me.taesu.kopringstandard.app.vo.SimpleI18n
import me.taesu.kopringstandard.user.domain.UserStatus
import me.taesu.kopringstandard.user.domain.UserType
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@RestController
class UserCreateController {
    @PostMapping("/api/v1/users")
    fun create(@Valid @RequestBody request: UserCreateRequest) {
        // create user
    }
}

class UserCreateRequest(
    @field:Email(message = "EXCEPTION.INVALID_FORMAT.USER_EMAIL")
    @field:NotBlank(message = "EXCEPTION.REQUIRED_PARAMETER")
    val email: String,
    @field:NotBlank(message = "EXCEPTION.REQUIRED_PARAMETER")
    val name: String,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val birthDate: LocalDate,
    val type: UserType,
    @field:Min(1, message = "EXCEPTION.INVALID_FORMAT.WEIGHT")
    @field:Max(999, message = "EXCEPTION.INVALID_FORMAT.WEIGHT")
    val weight: Int? = null,
    val nickname: String? = null,
    val status: UserStatus,
) {
    init {
        val baseBirthDate = LocalDate.of(1992, 1, 1)
        if (birthDate.isBefore(baseBirthDate)
            && type == UserType.DIAMOND
        ) {
            throwInvalidUserType(
                debugMessage = "$baseBirthDate 이전 출생자, $type 타입 등록 요청.",
                messageId = "EXCEPTION.USER_TYPE.DIAMOND.INVALID_BIRTH_DATE",
                messageArgs = arrayOf(baseBirthDate, SimpleI18n(type))
            )
        }
    }
}
