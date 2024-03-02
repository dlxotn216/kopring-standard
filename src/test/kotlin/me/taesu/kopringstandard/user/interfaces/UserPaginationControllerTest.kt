package me.taesu.kopringstandard.user.interfaces

import me.taesu.kopringstandard.user.application.UserPaginationService
import me.taesu.kopringstandard.user.application.any
import me.taesu.kopringstandard.user.domain.UserStatus
import me.taesu.kopringstandard.user.domain.UserType
import me.taesu.kopringstandard.user.infra.UserPaginateSqlResult
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
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
        given(service.paginate(any())).willReturn(
            PageImpl(
                listOf(
                    UserPaginateSqlResult(
                        userKey = 1L,
                        email = "taesu@crscube.co.kr",
                        name = "Lee Tae Su",
                        birthDate = LocalDate.of(1993, 2, 16),
                        null,
                        null,
                        type = UserType.DIAMOND,
                        status = UserStatus.ACTIVE
                    ),
                    UserPaginateSqlResult(
                        userKey = 2L,
                        email = "taesu2@crscube.co.kr",
                        name = "2 Tae Su",
                        birthDate = LocalDate.of(1993, 2, 16),
                        null,
                        null,
                        type = UserType.DIAMOND,
                        status = UserStatus.ACTIVE
                    )
                ),
                Pageable.ofSize(10),
                2
            )
        )

        // when
        val perform = this.mockMvc.get("/api/v1/users") {
            accept = MediaType.APPLICATION_JSON
            header("Accept-Language", "ko")
            param("email.value", "lee%kim")
            param("userName.empty", "true")
            param("userKeys.value", "1,2,3")
            param("userStatus", "A")
            param("userTypes.value", "DIAMOND,BRONZE")
        }

        // then
        perform.andExpect {
            status { isOk() }
            jsonPath("$.result.contents") { isArray() }
            jsonPath("$.result.contents[0].userKey") { value(1) }
            jsonPath("$.result.contents[0].email") { value("taesu@crscube.co.kr") }
            jsonPath("$.result.contents[0].name") { value("Lee Tae Su") }
            jsonPath("$.result.contents[0].birthDate") { value("1993-02-16") }
            jsonPath("$.result.contents[0].weight") { value(null) }
            jsonPath("$.result.contents[0].nickname") { value(null) }
            jsonPath("$.result.contents[0].type.value") { value("DIAMOND") }
            jsonPath("$.result.contents[0].type.label") { value("다이아몬드") }
            jsonPath("$.result.contents[0].status") { value("A") }
        }.andDo {
            print()
        }
    }

}
