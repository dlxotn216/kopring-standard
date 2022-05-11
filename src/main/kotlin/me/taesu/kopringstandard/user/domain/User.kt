package me.taesu.kopringstandard.user.domain

import org.springframework.data.repository.CrudRepository
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
    val userKey: Long,

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

interface UserRepository: CrudRepository<User, Long> {
    fun findByUserKey(userKey: Long): User?
}

fun UserRepository.findByUserKeyOrThrow(userKey: Long): User {
    return findByUserKey(userKey) ?: throw NoSuchElementException("User[$userKey]")
}
