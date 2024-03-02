package me.taesu.kopringstandard.user.interfaces

import me.taesu.kopringstandard.app.interfaces.PaginatedResult
import me.taesu.kopringstandard.app.interfaces.SuccessResponse
import me.taesu.kopringstandard.app.interfaces.SuccessResponseFactory
import me.taesu.kopringstandard.user.application.UserPaginationService
import me.taesu.kopringstandard.user.domain.UserType
import me.taesu.kopringstandard.user.infra.UserPaginateSqlCriteria
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
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
        @RequestParam parameters: Map<String, String>,
        criteria: UserPaginateSqlCriteria,
    ): SuccessResponse<PaginatedResult> {
        return SuccessResponseFactory.of(
            pageResult = service.paginate(criteria),
            parameters = parameters,
            criteria = criteria,
        )
    }
}
