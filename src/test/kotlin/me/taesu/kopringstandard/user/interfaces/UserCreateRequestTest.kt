package me.taesu.kopringstandard.user.interfaces

import me.taesu.kopringstandard.app.exception.ErrorCode
import me.taesu.kopringstandard.app.exception.InvalidRequestException
import me.taesu.kopringstandard.user.domain.UserStatus
import me.taesu.kopringstandard.user.domain.UserType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
class UserCreateRequestTest {
    @Test
    fun `기준 출생일 이전 사용자는 다이아몬드 등급이 될 수 없다`() {
        // given, when
        val exception = org.junit.jupiter.api.assertThrows<InvalidRequestException> {
            UserCreateRequest(
                email = "taesu@lle.com",
                name = "이태수",
                birthDate = LocalDate.of(1990, 1, 1),
                type = UserType.DIAMOND,
                null,
                null,
                status = UserStatus.ACTIVE,
            )
        }

        // then
        assertThat(exception.errorCode).isEqualTo(ErrorCode.INVALID_USER_TYPE)
    }

}
