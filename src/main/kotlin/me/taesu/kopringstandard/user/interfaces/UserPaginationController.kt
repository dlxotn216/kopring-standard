package me.taesu.kopringstandard.user.interfaces

import me.taesu.kopringstandard.app.interfaces.PageableCriteria
import me.taesu.kopringstandard.user.application.UserPaginationService
import me.taesu.kopringstandard.user.domain.UserType
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/**
 * Created by itaesu on 2022/05/24.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@RestController
class UserPaginationController(private val service: UserPaginationService) {
    @GetMapping("/api/v1/users")
    fun pagination(
        criteria: UserCriteria
    ): Page<UserPaginatedRow> {
        return service.pagination(criteria)
    }
}

class UserCriteria(
    page: Int = 1,
    size: Int = 10
): PageableCriteria(page, size, false)

class UserPaginatedRow(
    val userKey: Long,
    val email: String,
    val name: String,
    val birthDate: LocalDate,
    val userType: UserType?
)

