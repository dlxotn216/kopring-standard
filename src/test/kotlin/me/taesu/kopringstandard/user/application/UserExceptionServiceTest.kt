package me.taesu.kopringstandard.user.application

import me.taesu.kopringstandard.app.exception.UserEmailDuplicatedException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles

/**
 * Created by itaesu on 2022/05/18.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@EnabledIfSystemProperty(named = "testMode", matches = "unitTest")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
internal class UserExceptionServiceTest {
    @Spy
    @InjectMocks
    private lateinit var service: UserExceptionService

    @Test
    fun `duReturn은 실제 호출이 일어나지 않는다`() {
        // given
        doReturn("test").`when`(service).update()

        // then
        val result = service.update()

        // then
        assertThat(result).isEqualTo("test")
    }

    @Test
    fun `when thenReturn은 실제 호출이 일어난다`() {
        // given
        try {
            `when`(service.update()).thenReturn("test")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(UserEmailDuplicatedException::class.java)
        }
    }

    @Test
    fun `BDDMockito도 실제 호출이 일어난다`() {
        // given
        try {
            given(service.update()).willReturn("test")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(UserEmailDuplicatedException::class.java)
        }
    }

}