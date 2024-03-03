package me.taesu.kopringstandard.user.application

import me.taesu.kopringstandard.user.domain.UserRepository
import me.taesu.kopringstandard.user.infra.UserPaginateSqlCriteria
import me.taesu.kopringstandard.user.infra.UserPaginateSqlResult
import me.taesu.kopringstandard.user.infra.UserQuery
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserPaginationService(private val userQuery: UserQuery,
                            private val userRepository: UserRepository) {
    fun paginate(criteria: UserPaginateSqlCriteria): Page<UserPaginateSqlResult> {
        val findAll = userRepository.findAll()
        return userQuery.paginateUsers(criteria)
    }
}
