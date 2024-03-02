package me.taesu.kopringstandard.app.jdbc

import me.taesu.kopringstandard.app.query.DateRangeParameters
import me.taesu.kopringstandard.app.query.DateRangeParametersColumnType
import me.taesu.kopringstandard.app.query.LikeParameters
import me.taesu.kopringstandard.app.query.ListParameters
import me.taesu.kopringstandard.app.vo.KeyPair

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
infix fun String.eq(bindVariable: String): String {
    return "and $this = :$bindVariable"
}

infix fun String.eqValue(value: String): String {
    return "and$ this = '$value'"
}

fun <T> String.`in`(
    bindVariable: String,
    parameters: ListParameters<T>,
    operator: String = "and",
): String {
    val entries = parameters.value
    if (entries.isEmpty()) {
        return ""
    }
    val withNull = entries.any { it == null || it == "" } || parameters.withNull
    val entriesSet = entries.filterNot { it == null || it == "" }.toSet()
    if (entriesSet.isEmpty()) {
        // onlyIncludeNull
        return "$operator $this is null"
    }

    // col in :variable[0] or col in :variable[1]
    val inClause = List(entriesSet.chunked(1000).size) { index ->
        "$this in (:_$bindVariable[$index])"
    }.joinToString(" or ")

    return when (withNull) {
        true -> "$operator ($inClause or $this is null)"
        false -> "$operator ($inClause)"
    }
}

fun <T> String.notIn(
    bindVariable: String,
    parameters: ListParameters<T>,
    operator: String = "and",
): String {
    val entries = parameters.value
    if (entries.isEmpty()) {
        return ""
    }
    val entriesSet = entries.filterNotNull().toSet()

    // COL not in :variable[0]
    val notInClause = List(entriesSet.chunked(1000).size) { index ->
        "$this not in (:_$bindVariable[$index])"
    }.joinToString(" and ")

    return "$operator ($notInClause)"
}

fun keyPairIn(
    column1: String,
    column2: String,
    keyPairs: List<KeyPair>,
): String {
    if (keyPairs.isEmpty()) {
        return ""
    }

    return keyPairs.joinToString(",", "and ($column1, $column2) in (", ")") {
        "(${it.key1}, ${it.key2})"
    }
}

fun keyPairNotIn(
    column1: String,
    column2: String,
    keyPairs: Collection<KeyPair>,
): String {
    if (keyPairs.isEmpty()) {
        return ""
    }

    return keyPairs.joinToString(",", "and ($column1, $column2) not in (", ")") {
        "(${it.key1}, ${it.key2})"
    }
}

fun String.like(
    bindVariable: String,
    likeParameters: LikeParameters,
    operator: String = "and",
    nullString: String? = null,
): String {
    return when (likeParameters.empty) {
        true -> {
            val concatOperator = " or "
            // operator (column is null or column = 'nullstring')
            concatClauses(
                clauses = listOfNotNull(
                    "$this is null",
                    nullString?.let { "$this = '$it'" },
                ),
                concatOperator = concatOperator,
                operator = operator
            )
        }

        false -> {
            val concatOperator = " and "
            // operator (column like 'value' and column <> 'nullstring')
            concatClauses(
                clauses = listOfNotNull(
                    resolveLikeClause(likeParameters.value.trim(), bindVariable),
                    nullString?.let { "$this <> '$it'" },
                ),
                concatOperator = concatOperator,
                operator = operator
            )
        }
    }
}

/**
 * @return 조건절의 개수에 따른 결합문
 */
private fun concatClauses(
    clauses: List<String>,
    concatOperator: String,
    operator: String,
) = when {
    clauses.isEmpty() -> ""
    clauses.size == 1 -> clauses.joinToString(concatOperator, "$operator ")
    else -> clauses.joinToString(concatOperator, "$operator (", ")")
}


/**
 * @return value에 따라 Like 복합 필터 혹은 Like 단일 필터 구문
 */
private fun String.resolveLikeClause(value: String, bindVariable: String): String? {
    return if (value.contains("%")) {
        resolveComplexOrClauses(value, bindVariable)
    } else {
        resolveSimpleLikeClause(bindVariable)
    }
}

/**
 * @return (simpleClause(variable[0]) or simpleClause(variable[1]) ...)
 */
private fun String.resolveComplexOrClauses(value: String, bindVariable: String) =
    List(value.split("%").filter { it.isNotBlank() }.size) { index ->
        resolveSimpleLikeClause("$bindVariable[$index]")
    }.takeIf { it.isNotEmpty() }?.joinToString(" or ", "(", ")")

/**
 * @return lower($this) like '%:variable%' escape \'
 */
private fun String.resolveSimpleLikeClause(bindVariable: String) =
    "lower($this) like '%'||:$bindVariable||'%' escape '\\'"

/**
 * DateRangeParameters의 startDate, endDate는 LocalDate 유형으로 바인드 시 SQL type 91 (DATE) 유형으로 바인드 된다.
 *
 * @param bindVariable 바인드 할 변수 이름
 * @param dateRangeFilter DateRangeParameters Filter Dto
 * @param columnType Column Type
 */
fun String.dateRange(
    bindVariable: String,
    dateRangeFilter: DateRangeParameters,
    columnType: DateRangeParametersColumnType = DateRangeParametersColumnType.STRING,
): String {
    return when (columnType) {
        DateRangeParametersColumnType.STRING -> resolveStringTypeDateRange(dateRangeFilter, bindVariable)
        DateRangeParametersColumnType.DATE -> resolveDateTypeDateRange(dateRangeFilter, bindVariable)
    }
}

/**
 * @param bindVariable 바인드 할 변수 이름
 */
private fun String.resolveStringTypeDateRange(
    dateRangeFilter: DateRangeParameters,
    bindVariable: String,
) = when {
    dateRangeFilter.empty -> "and $this is null"
    dateRangeFilter.betweenMatched -> "and $this between to_char(:$bindVariable.startDate, 'YYYY-MM-DD') and to_char(:$bindVariable.endDate, 'YYYY-MM-DD')"
    dateRangeFilter.startDate != null -> "and $this >= to_char(:$bindVariable.startDate, 'YYYY-MM-DD')"
    dateRangeFilter.endDate != null -> "and $this <= to_char(:$bindVariable.endDate, 'YYYY-MM-DD')"
    else -> ""
}

/**
 * @param bindVariable 바인드 할 변수 이름, 타입이 LocalDate이어야 함.
 */
private fun String.resolveDateTypeDateRange(
    dateRangeFilter: DateRangeParameters,
    bindVariable: String,
) = when {
    dateRangeFilter.empty -> "and $this is null"
    dateRangeFilter.betweenMatched -> "and $this between :$bindVariable.startDate and to_date(to_char(:$bindVariable.endDate, 'YYYY-MM-DD') || ' 23:59:59', 'YYYY-MM-DD HH24:MI:SS')"
    dateRangeFilter.startDate != null -> "and $this >= :$bindVariable.startDate"
    dateRangeFilter.endDate != null -> "and $this <= to_date(to_char(:$bindVariable.endDate, 'YYYY-MM-DD') || ' 23:59:59', 'YYYY-MM-DD HH24:MI:SS')"
    else -> ""
}
