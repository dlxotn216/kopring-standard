package me.taesu.kopringstandard.user.infra

import me.taesu.kopringstandard.app.query.*
import me.taesu.kopringstandard.user.domain.UserStatus
import me.taesu.kopringstandard.user.domain.UserType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import java.time.LocalDate

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
interface UserQuery {
    fun selectUser(criteria: UserSelectSqlCriteria): UserSelectSqlResult?

    fun paginateUsers(criteria: UserPaginateSqlCriteria): Page<UserPaginateSqlResult>
}

class UserSelectSqlCriteria(
    val userKey: Long,
)

class UserSelectSqlResult(
    val userKey: Long,
    val email: String,
    val name: String,
    val birthDate: LocalDate,
    val weight: Int?,
    val nickname: String?,
    val type: UserType,
    val status: UserStatus,
)

class UserPaginateSqlCriteria(
    page: Int = DEFAULT_PAGE,
    size: Int = DEFAULT_SIZE,
    var userKeys: LongListParameters = LongListParameters(),
    var email: LikeParameters? = null,
    var userName: LikeParameters? = null,
    var userStatus: UserStatus? = null,
    var userTypes: UserTypeListParameters = UserTypeListParameters(),
): PageableCriteria(page, size) {
    override val sort: List<Sortable> = listOf(UserSort.USER_KEY_ASC)

    enum class UserSort(
        override val columnName: String,
        override val direction: Sort.Direction,
    ): Sortable {
        USER_KEY_ASC("user_key", Sort.Direction.ASC),
    }
}

class UserPaginateSqlResult(
    val userKey: Long,
    val email: String,
    val name: String,
    val birthDate: LocalDate,
    val weight: Int?,
    val nickname: String?,
    val type: UserType,
    val status: UserStatus,
): PaginatedRow()
