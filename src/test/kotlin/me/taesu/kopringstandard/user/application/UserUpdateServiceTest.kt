package me.taesu.kopringstandard.user.application

import me.taesu.kopringstandard.user.domain.User
import me.taesu.kopringstandard.user.domain.UserRepository
import me.taesu.kopringstandard.user.domain.findByUserKeyOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

/**
 * Created by itaesu on 2022/05/10.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
internal class UserUpdateServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository
    @InjectMocks
    @Spy
    private lateinit var userUpdateService: UserUpdateService

    @Test
    fun `Should success to update`() {
        // given
        val user = User(userKey = 12L, name = "lee", birthDate = LocalDate.of(1993, 2, 16))
        given(userRepository.findByUserKey(12L)).willReturn(user)

        // when
        userUpdateService.update(12L, UserUpdateRequest("changed", LocalDate.of(1993, 2, 11)))

        // then
        assertThat(user.name).isEqualTo("changed")
        assertThat(user.birthDate).isEqualTo(LocalDate.of(1993, 2, 11))
    }
}