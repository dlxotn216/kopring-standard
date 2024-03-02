package me.taesu.kopringstandard.app.jdbc

import me.taesu.kopringstandard.app.query.PageableCriteria
import me.taesu.kopringstandard.app.query.PaginatedRow
import me.taesu.kopringstandard.app.query.Sortable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */

fun <T: PaginatedRow> PageableCriteria.paginate(
    countQuery: () -> Long,
    fetchQuery: () -> List<T>,
): Page<T> {
    val count = countQuery()
    return if (count == 0L) {
        PageImpl(emptyList(), this.pageRequest, count)
    } else {
        PageImpl(fetchQuery(), this.pageRequest, count)
    }
}

fun NamedParameterJdbcTemplate.doCount(
    criteria: PageableCriteria,
    query: String,
    withClause: String = "",
): Long {
    return this.queryForObject(
        """
            $withClause
            SELECT COUNT(*) COUNT 
            FROM (
              $query
            )
        """.trimIndent(),
        CustomBeanPropertySqlParameterSource(criteria),
    ) { it, _ -> it long "COUNT" } ?: 0L
}

fun <T> NamedParameterJdbcTemplate.doFetch(
    criteria: PageableCriteria,
    query: String,
    withClause: String = "",
    sort: List<Sortable>,
    mapper: (ResultSet, Int) -> T,
): MutableList<T> {
    return this.doFetch(
        criteria = criteria,
        query = query,
        withClause = withClause,
        sortClause = resolveSortClause(sort),
        mapper = mapper
    )
}

fun <T> NamedParameterJdbcTemplate.doFetch(
    criteria: PageableCriteria,
    query: String,
    withClause: String = "",
    sortClause: String,
    mapper: (ResultSet, Int) -> T,
): MutableList<T> {
    return this.query(
        """
            $withClause
            $query
            $sortClause
            ${paginationClause(":offset", ":size")}
        """.trimIndent(),
        CustomBeanPropertySqlParameterSource(criteria),
        mapper
    )
}

fun paginationClause(offset: String, size: String) = "offset $offset rows fetch first $size rows only"

fun resolveSortClause(
    sort: List<Sortable>,
    defaultSortTypes: List<Sortable> = emptyList(),
): String {
    if (sort.isEmpty() && defaultSortTypes.isEmpty()) {
        return ""
    }
    return "order by ${sort.resolve(*defaultSortTypes.toTypedArray())}"
}

fun Collection<Sortable>.resolve(vararg defaultSortTypes: Sortable) =
    (this + defaultSortTypes).joinToString(", ") { "${it.columnName} ${it.direction}" }
