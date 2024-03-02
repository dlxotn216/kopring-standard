package me.taesu.kopringstandard.user.domain

import me.taesu.kopringstandard.app.converters.UserStatusConverter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

/**
 * Created by itaesu on 2022/05/10.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@Table(name = "usr_user")
@Entity(name = "User")
class User(
    @Id
    @GeneratedValue
    @Column(name = "user_key")
    val key: Long = 0L,

    @Embedded
    private val userInfo: UserInfo
) {
    val email: String get() = userInfo.email
    val name: String get() = userInfo.name
    val birthDate: LocalDate get() = userInfo.birthDate
    val type: UserType get() = userInfo.type
    val status: UserStatus get() = userInfo.status

    constructor(
        key: Long = 0,
        email: String,
        name: String,
        birthDate: LocalDate,
        type: UserType,
    ):
        this(
            key = key,
            userInfo = UserInfo(
                email = email,
                name = name,
                birthDate = birthDate,
                type = type,
                status = UserStatus.ACTIVE
            )
        )

    fun update(email: String, name: String, birthDate: LocalDate) {
        this.userInfo.update(email, name, birthDate)
    }
}

@Embeddable
class UserInfo(
    @Column(name = "email", unique = true, nullable = false)
    var email: String,

    @Column(name = "user_name", nullable = false)
    var name: String,

    @Column(name = "birth_date", nullable = false)
    var birthDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: UserType,

    @Column(name = "weight")
    var weight: Int? = null,

    @Column(name = "nick_name")
    var nickname: String? = null,

    @Convert(converter = UserStatusConverter::class)
    @Column(name = "status", nullable = false)
    var status: UserStatus,
): Serializable {
    fun update(email: String, name: String, birthDate: LocalDate) {
        this.email = email
        this.name = name
        this.birthDate = birthDate
    }
}

@NoRepositoryBean
interface KeyBasedRepository<T, Long>: JpaRepository<T, Long> {
    fun findByKey(key: Long): T?
}

fun <T> KeyBasedRepository<T, Long>.findByKeyOrThrow(key: Long): T {
    return findByKey(key) ?: throw NoSuchElementException("Entity[$key]")
}

interface UserRepository: KeyBasedRepository<User, Long> {
    fun existsByUserInfoEmail(email: String): Boolean
}
