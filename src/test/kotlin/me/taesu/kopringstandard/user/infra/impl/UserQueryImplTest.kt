package me.taesu.kopringstandard.user.infra.impl

import me.taesu.kopringstandard.user.domain.User
import me.taesu.kopringstandard.user.domain.UserRepository
import me.taesu.kopringstandard.user.domain.UserStatus
import me.taesu.kopringstandard.user.domain.UserType
import me.taesu.kopringstandard.user.infra.UserQuery
import me.taesu.kopringstandard.user.infra.UserSelectSqlCriteria
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@ActiveProfiles("test")
@SpringBootTest
class UserQueryImplTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userQuery: UserQuery

    var user: User? = null

    @BeforeEach
    fun init() {
        userRepository.deleteAll()
        this.user = userRepository.save(
            User(
                email = "taesu@crscube.co.kr",
                name = "lee",
                birthDate = LocalDate.of(1993, 2, 16),
                type = UserType.BRONZE
            )
        )
    }

    @Test
    fun `Native sql 조회 테스트`() {
        // given
        val userKey = user!!.key

        // when
        val user = userQuery.selectUser(UserSelectSqlCriteria(userKey))

        // then
        user!!.run {
            assertThat(this.userKey).isEqualTo(userKey)
            assertThat(this.email).isEqualTo("taesu@crscube.co.kr")
            assertThat(this.name).isEqualTo("lee")
            assertThat(this.birthDate).isEqualTo(LocalDate.of(1993, 2, 16))
            assertThat(this.weight).isNull()
            assertThat(this.nickname).isNull()
            assertThat(this.type).isEqualTo(UserType.BRONZE)
            assertThat(this.status).isEqualTo(UserStatus.ACTIVE)
        }
    }
}
