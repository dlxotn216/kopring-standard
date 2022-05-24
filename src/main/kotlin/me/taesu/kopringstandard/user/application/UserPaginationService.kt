package me.taesu.kopringstandard.user.application

import me.taesu.kopringstandard.user.domain.User
import me.taesu.kopringstandard.user.domain.UserRepository
import me.taesu.kopringstandard.user.interfaces.UserCriteria
import me.taesu.kopringstandard.user.interfaces.UserPaginatedRow
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = false)
class UserPaginationService(private val userRepository: UserRepository) {
    fun pagination(criteria: UserCriteria): Page<UserPaginatedRow> {
        return userRepository.findAll(criteria.pageRequest)
            .map { it.toPaginatedRow() }
    }
}

fun User.toPaginatedRow(): UserPaginatedRow {
    return UserPaginatedRow(
        userKey = key,
        email = email,
        name = name,
        birthDate = birthDate,
        userType = type
    )
}