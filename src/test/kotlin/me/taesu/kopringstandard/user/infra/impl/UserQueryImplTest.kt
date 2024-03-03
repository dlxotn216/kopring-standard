package me.taesu.kopringstandard.user.infra.impl

import me.taesu.kopringstandard.app.query.LikeParameters
import me.taesu.kopringstandard.app.query.LongListParameters
import me.taesu.kopringstandard.setTimezone
import me.taesu.kopringstandard.user.domain.User
import me.taesu.kopringstandard.user.domain.UserRepository
import me.taesu.kopringstandard.user.domain.UserStatus
import me.taesu.kopringstandard.user.domain.UserType
import me.taesu.kopringstandard.user.infra.UserPaginateSqlCriteria
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

    init {
        setTimezone()
    }

    @BeforeEach
    fun init() {
        userRepository.deleteAll()
        this.user = userRepository.save(
            User(
                email = "tae_su_lee@crscube.co.kr",
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
            assertThat(this.email).isEqualTo("tae_su_lee@crscube.co.kr")
            assertThat(this.name).isEqualTo("lee")
            assertThat(this.birthDate).isEqualTo(LocalDate.of(1993, 2, 16))
            assertThat(this.weight).isNull()
            assertThat(this.nickname).isNull()
            assertThat(this.type).isEqualTo(UserType.BRONZE)
            assertThat(this.status).isEqualTo(UserStatus.ACTIVE)
        }
    }

    @Test
    fun `Paginate sql 조회 테스트`() {
        // given
        val criteria = UserPaginateSqlCriteria(
            userKeys = LongListParameters((1L..2000L).toList(), withNull = true),
            userName = LikeParameters("lee"),
            userStatus = UserStatus.ACTIVE,
        )

        // when
        val page = userQuery.paginateUsers(criteria)

        // then
        assertThat(page.totalElements).isEqualTo(1L)
        page.content[0].run {
            assertThat(this.userKey).isEqualTo(userKey)
            assertThat(this.email).isEqualTo("tae_su_lee@crscube.co.kr")
            assertThat(this.name).isEqualTo("lee")
            assertThat(this.birthDate).isEqualTo(LocalDate.of(1993, 2, 16))
            assertThat(this.weight).isNull()
            assertThat(this.nickname).isNull()
            assertThat(this.type).isEqualTo(UserType.BRONZE)
            assertThat(this.status).isEqualTo(UserStatus.ACTIVE)
        }
    }

    @Test
    fun `확장된 Like sql 조회 테스트`() {
        // given
        val criteria = UserPaginateSqlCriteria(
            userName = LikeParameters("lee%kim"),
        )

        // when
        val page = userQuery.paginateUsers(criteria)

        // then
        assertThat(page.totalElements).isEqualTo(1L)
        page.content[0].run {
            assertThat(this.userKey).isEqualTo(userKey)
            assertThat(this.email).isEqualTo("tae_su_lee@crscube.co.kr")
            assertThat(this.name).isEqualTo("lee")
            assertThat(this.birthDate).isEqualTo(LocalDate.of(1993, 2, 16))
            assertThat(this.weight).isNull()
            assertThat(this.nickname).isNull()
            assertThat(this.type).isEqualTo(UserType.BRONZE)
            assertThat(this.status).isEqualTo(UserStatus.ACTIVE)
        }
    }

    @Test
    fun `_를 포함하는 Like sql 조회 테스트`() {
        // given
        val criteria = UserPaginateSqlCriteria(
            email = LikeParameters("tae_su_lee"),
        )

        // when
        val page = userQuery.paginateUsers(criteria)

        // then
        assertThat(page.totalElements).isEqualTo(1L)
        page.content[0].run {
            assertThat(this.userKey).isEqualTo(userKey)
            assertThat(this.email).isEqualTo("tae_su_lee@crscube.co.kr")
            assertThat(this.name).isEqualTo("lee")
            assertThat(this.birthDate).isEqualTo(LocalDate.of(1993, 2, 16))
            assertThat(this.weight).isNull()
            assertThat(this.nickname).isNull()
            assertThat(this.type).isEqualTo(UserType.BRONZE)
            assertThat(this.status).isEqualTo(UserStatus.ACTIVE)
        }
    }
}
