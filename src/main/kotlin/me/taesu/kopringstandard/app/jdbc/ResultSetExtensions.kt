package me.taesu.kopringstandard.app.jdbc

import me.taesu.kopringstandard.app.converters.RawCodeConverter
import java.sql.ResultSet
import java.time.LocalDate

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */

infix fun ResultSet.longOrNull(columnName: String): Long? {
    return wasNull(this, getLong(columnName))
}

infix fun ResultSet.long(columnName: String): Long {
    return this.getLong(columnName)
}

infix fun ResultSet.intOrNull(columnName: String): Int? {
    return wasNull(this, getInt(columnName))
}

infix fun ResultSet.int(columnName: String): Int {
    return this.getInt(columnName)
}

infix fun ResultSet.stringOrNull(columnName: String): String? {
    return wasNull(this, getString(columnName))
}

infix fun ResultSet.string(columnName: String): String {
    return this.getString(columnName)
}

infix fun ResultSet.localDateOrNull(columnName: String): LocalDate? {
    return wasNull(this, localDate(columnName))
}

infix fun ResultSet.localDate(columnName: String): LocalDate {
    return this.getDate(columnName).toLocalDate()
}

infix fun ResultSet.rawCode(enumType: Class<out Enum<*>>): ResultSetRawCode {
    return ResultSetRawCode(enumType, this)
}

class ResultSetRawCode(
    val enumType: Class<out Enum<*>>,
    val rs: ResultSet,
) {
    infix fun from(columnName: String): Enum<*> {
        return RawCodeConverter.fromRawCode(enumType, rs.getString(columnName))
    }
}

fun <T> wasNull(resultSet: ResultSet, value: T) = when {
    resultSet.wasNull() -> null
    else -> value
}
