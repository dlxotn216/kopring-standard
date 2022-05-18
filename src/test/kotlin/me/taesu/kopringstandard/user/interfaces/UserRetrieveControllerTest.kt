package me.taesu.kopringstandard.user.interfaces

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.*
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate

/**
 * Created by itaesu on 2022/05/18.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@WebMvcTest(
    value = [UserRetrieveController::class],
)
internal class UserRetrieveControllerTest {
    @MockBean
    private lateinit var userRetrieveService: UserRetrieveService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `Parameter Stub`() {
        // given
        given(userRetrieveService.retrieve(12L)).willReturn(
            UserRetrieveResponse(
                key = 12L,
                email = "taesu@crscube.co.kr",
                name = "Lee",
                birthDate = LocalDate.of(1993, 2, 16)
            )
        )

        // when
        val perform = this.mockMvc.get("/api/v1/studies/1/users/12") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }

        // then
        perform.andExpect {
            status { isOk() }
            jsonPath("$.key") { value(12) }
            jsonPath("$.email") { value("taesu@crscube.co.kr") }
            jsonPath("$.name") { value("Lee") }
            jsonPath("$.birthDate") { value("1993-02-16") }
        }.andDo {
            print()
        }
    }

    @Test
    fun `Parameter Stub with any, eq`() {
        // given
        given(userRetrieveService.retrieve(eq((12L)), anyString())).willReturn(
            UserRetrieveResponse(
                key = 12L,
                email = "taesu@crscube.co.kr",
                name = "Lee",
                birthDate = LocalDate.of(1993, 2, 16)
            )
        )

        // when
        val perform = this.mockMvc.get("/api/v1/studies/12/users") {
            param("email", "taesu@crscube.co.kr")
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }

        // then
        perform.andExpect {
            status { isOk() }
            jsonPath("$.key") { value(12) }
            jsonPath("$.email") { value("taesu@crscube.co.kr") }
            jsonPath("$.name") { value("Lee") }
            jsonPath("$.birthDate") { value("1993-02-16") }
        }.andDo {
            print()
        }
    }

    @Test
    fun `Parameter Stub with any, no eq`() {
        // given
        // any와 함께 실제 값 12를 전달하면 InvalidUseOfMatchersException가 발생
        // any와 함께 쓰려면 eq(12)로 해야 함.
        try {
            given(userRetrieveService.retrieve(12, anyString())).willReturn(
                UserRetrieveResponse(
                    key = 12L,
                    email = "taesu@crscube.co.kr",
                    name = "Lee",
                    birthDate = LocalDate.of(1993, 2, 16)
                )
            )
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(InvalidUseOfMatchersException::class.java)
        }
    }

    @Test
    fun `Parameter Stub with only eq`() {
        // given
        // eq로만 스텁 채워도 NullPointerException, InvalidUseOfMatchersException가 발생
        try {
            given(
                userRetrieveService.retrieve(
                    ArgumentMatchers.eq(12L),
                    ArgumentMatchers.eq("taesu@crscube.co.kr")
                )
            ).willReturn(
                UserRetrieveResponse(
                    key = 12L,
                    email = "taesu@crscube.co.kr",
                    name = "Lee",
                    birthDate = LocalDate.of(1993, 2, 16)
                )
            )
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(NullPointerException::class.java)
        }
    }
}