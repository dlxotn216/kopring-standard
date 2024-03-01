package me.taesu.kopringstandard.user.interfaces

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@EnabledIfSystemProperty(named = "testMode", matches = "unitTest")
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@WebMvcTest(
    value = [UserStatusTestController::class],
)
class UserStatusTestControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `Request Body로 전달된 RawCode UserStatus가 적절히 변환된다`() {
        // given
        val body = """{
            |"status": "A"
        }""".trimMargin()

        // when
        val perform = this.mockMvc.post("/api/v1/users/status") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            header("Accept-Language", "ko")
            content = body
        }

        // then
        perform.andExpect {
            status { isOk() }
            jsonPath("$.status") { value("A") }
        }.andDo {
            print()
        }
    }
}
