package me.taesu.kopringstandard.user.domain

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
@Table(name = "USR_USER")
@Entity(name = "User")
class User(
    @Id
    @GeneratedValue
    @Column(name = "USER_KEY")
    val key: Long,

    @Embedded
    private val userInfo: UserInfo
) {
    val name: String get() = userInfo.name
    val birthDate: LocalDate get() = userInfo.birthDate

    constructor(userKey: Long = 0, name: String, birthDate: LocalDate):
        this(userKey, userInfo = UserInfo(name, birthDate))

    fun update(name: String, birthDate: LocalDate) {
        this.userInfo.update(name, birthDate)
    }
}

@Embeddable
class UserInfo(
    @Column(name = "USER_NAME")
    var name: String,

    @Column(name = "BIRTH_DATE")
    var birthDate: LocalDate,
): Serializable {
    fun update(name: String, birthDate: LocalDate) {
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

interface UserRepository: KeyBasedRepository<User, Long>
