package me.taesu.kopringstandard.user.domain

import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

/**
 * Created by itaesu on 2022/05/10.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
// @ExtendWith(SpringExtension::class)
// @DataJpaTest
// @ActiveProfiles("test")
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @AutoConfigureJson
internal class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    // @Test
    fun `Should success to retrieve`() {
        // given
        val user = userRepository.save(
            User(
                email = "taesu@crscube.co.kr",
                name = "lee",
                birthDate = LocalDate.of(1993, 2, 16),
                type = UserType.BRONZE
            )
        )

        // when
        val findBy = userRepository.findByKeyOrThrow(user.key)

        // then
        assertThat(findBy.name).isEqualTo("lee")
        assertThat(findBy.birthDate).isEqualTo(LocalDate.of(1993, 2, 16))
    }
}