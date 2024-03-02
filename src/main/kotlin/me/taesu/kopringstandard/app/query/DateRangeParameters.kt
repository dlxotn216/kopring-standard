package me.taesu.kopringstandard.app.query

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
class DateRangeParameters(
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var startDate: LocalDate? = null,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var endDate: LocalDate? = null,
    var empty: Boolean = false,
) {
    val betweenMatched get() = startDate != null && endDate != null && !empty
}

enum class DateRangeParametersColumnType {
    STRING,
    DATE
    ;
}
