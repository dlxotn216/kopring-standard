package me.taesu.kopringstandard.user.infra.impl

import me.taesu.kopringstandard.app.jdbc.*
import me.taesu.kopringstandard.user.domain.UserStatus
import me.taesu.kopringstandard.user.domain.UserType
import me.taesu.kopringstandard.user.infra.UserQuery
import me.taesu.kopringstandard.user.infra.UserSelectSqlCriteria
import me.taesu.kopringstandard.user.infra.UserSelectSqlResult
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@Component
class UserQueryImpl(private val jdbcTemplate: NamedParameterJdbcTemplate): UserQuery {
    override fun selectUser(criteria: UserSelectSqlCriteria): UserSelectSqlResult? {
        return jdbcTemplate.queryForObject(
            compileSelectUser(criteria, debug = true),
            CustomBeanPropertySqlParameterSource(criteria)
        ) { rs, _ ->
            UserSelectSqlResult(
                rs long "user_key",
                rs string "email",
                rs string "user_name",
                rs localDate "birth_date",
                rs intOrNull "weight",
                rs stringOrNull "nick_name",
                (rs rawCode UserType::class.java from "type") as UserType,
                (rs rawCode UserStatus::class.java from "status") as UserStatus,
            )
        }
    }

    private fun compileSelectUser(criteria: UserSelectSqlCriteria, debug: Boolean = false) = """
                    select user_key, email, user_name, birth_date
                      , type, weight, nick_name, status
                    from usr_user uu 
                    where uu.user_key = :userKey
                """.trimIndent()
}
