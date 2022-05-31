package me.taesu.kopringstandard.user.interfaces

import me.taesu.kopringstandard.user.application.UserPaginationService
import me.taesu.kopringstandard.user.application.any
import me.taesu.kopringstandard.user.domain.UserType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate

/**
 * Created by itaesu on 2022/05/24.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@EnabledIfSystemProperty(named = "testMode", matches = "unitTest")
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@WebMvcTest(
    value = [UserPaginationController::class],
)
internal class UserPaginationControllerTest {
    @MockBean
    private lateinit var service: UserPaginationService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `다국어 처리 된 사용자 타입 정보가 응답으로 조회된다`() {
        // given
        given(service.pagination(any())).willReturn(
            PageImpl(
                listOf(
                    UserPaginatedRow(
                        userKey = 1L,
                        email = "taesu@crscube.co.kr",
                        name = "Lee Tae Su",
                        birthDate = LocalDate.of(1993, 2, 16),
                        userType = UserType.DIAMOND
                    ),
                    UserPaginatedRow(
                        userKey = 23411L,
                        email = "taesu1ef@crscube.co.kr",
                        name = "Park Tae Su",
                        birthDate = LocalDate.of(1993, 2, 16),
                        userType = null
                    )
                )
            )
        )

        // when
        val perform = this.mockMvc.get("/api/v1/users") {
            accept = MediaType.APPLICATION_JSON
            header("Accept-Language", "ko")
        }

        // then
        perform.andExpect {
            status { isOk() }
            jsonPath("$.content") { isArray() }
            jsonPath("$.content[0].userKey") { value(1) }
            jsonPath("$.content[0].email") { value("taesu@crscube.co.kr") }
            jsonPath("$.content[0].name") { value("Lee Tae Su") }
            jsonPath("$.content[0].birthDate") { value("1993-02-16") }
            jsonPath("$.content[0].userType.value") { value("DIAMOND") }
            jsonPath("$.content[0].userType.label") { value("다이아몬드") }

            jsonPath("$.content[1].userType.value") { value("") }
            jsonPath("$.content[1].userType.label") { value("") }
        }.andDo {
            print()
        }
    }

}