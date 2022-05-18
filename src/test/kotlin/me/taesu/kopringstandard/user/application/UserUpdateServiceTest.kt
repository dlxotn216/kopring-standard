package me.taesu.kopringstandard.user.application

import me.taesu.kopringstandard.user.domain.User
import me.taesu.kopringstandard.user.domain.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
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
        val user =
            User(key = 12L, email = "taesu@crscube.co.kr", name = "lee", birthDate = LocalDate.of(1993, 2, 16))
        given(userRepository.existsByEmail("taesu@crscube.co.kr")).willReturn(false)
        given(userRepository.findByKey(12L)).willReturn(user)

        doReturn(123).`when`(userRepository).findByKey(12L)
        `when`(userRepository.findByKey(12L)).thenReturn(user)

        // when
        userUpdateService.update(
            12L, UserUpdateRequest(
                email = "taesu@crscube.co.kr",
                name = "changed",
                birthDate = LocalDate.of(1993, 2, 11)
            )
        )

        // then
        assertThat(user.name).isEqualTo("changed")
        assertThat(user.birthDate).isEqualTo(LocalDate.of(1993, 2, 11))
    }
}

fun <T> any(): T {
    BDDMockito.any<T>()
    return uninitialized()
}

private fun <T> uninitialized(): T = null as T

fun <T> eq(value: T): T {
    BDDMockito.any<T>()
    return uninitialized()
}
