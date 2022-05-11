package me.taesu.kopringstandard.user.domain

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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

/**
 * Created by itaesu on 2022/05/10.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@ExtendWith(SpringExtension::class)
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureJson
internal class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `Should success to retrieve`() {
        // given
        val user = userRepository.save(User(name = "lee", birthDate = LocalDate.of(1993, 2, 16)))

        // when
        val findBy = userRepository.findByUserKeyOrThrow(user.userKey)

        // then
        assertThat(findBy.name).isEqualTo("lee")
        assertThat(findBy.birthDate).isEqualTo(LocalDate.of(1993, 2, 16))
    }
}