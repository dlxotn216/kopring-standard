package me.taesu.kopringstandard.user.infra.impl

import me.taesu.kopringstandard.app.query.queryIf
import me.taesu.kopringstandard.app.jdbc.*
import me.taesu.kopringstandard.user.domain.UserStatus
import me.taesu.kopringstandard.user.domain.UserType
import me.taesu.kopringstandard.user.infra.*
import org.springframework.data.domain.Page
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
            """
                select user_key, email, user_name, birth_date
                  , type, weight, nick_name, status
                from usr_user uu 
                where uu.user_key = :userKey
            """.trimIndent(),
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

    override fun paginateUsers(criteria: UserPaginateSqlCriteria): Page<UserPaginateSqlResult> {
        val query = """
            select user_key, email, user_name, birth_date
              , type, weight, nick_name, status
            from usr_user uu 
            where 1 = 1
            ${
            listOfNotNull(
                criteria.email.queryIf { "email".like("email", it) },
                criteria.userName.queryIf { "user_name".like("userName", it) },
                criteria.userKeys.queryIf { "user_key".`in`("userKeys", it) },
                criteria.userStatus.queryIf { "status" eq "userStatus" },
                criteria.userTypes.queryIf { "type".`in`("userTypes", it) },
            ).joinToString("\n")
        }
        """.trimIndent()

        return criteria.paginate({ jdbcTemplate.doCount(criteria, query) }) {
            jdbcTemplate.doFetch(
                criteria = criteria,
                query = query,
                sort = criteria.sort
            ) { rs, _ ->
                UserPaginateSqlResult(
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
    }
}
