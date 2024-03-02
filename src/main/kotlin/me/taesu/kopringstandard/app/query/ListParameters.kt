package me.taesu.kopringstandard.app.query

import com.fasterxml.jackson.annotation.JsonIgnore
import me.taesu.kopringstandard.app.domain.RawCode
import me.taesu.kopringstandard.user.domain.UserType

open class ListParameters<T>(
    var value: List<T> = emptyList(),
    var withNull: Boolean = false,
): Filterable {
    @get:JsonIgnore
    override val filterable get() = this.value.isNotEmpty() || this.withNull
}

class LongListParameters(
    value: List<Long> = emptyList(),
    withNull: Boolean = false,
): Filterable, ListParameters<Long>(value, withNull)

class StringListParameters(
    value: List<String> = emptyList(),
    withNull: Boolean = false,
): Filterable, ListParameters<String>(value, withNull)

open class RawCodeListParameters<T: RawCode>(
    value: List<T> = emptyList(),
    withNull: Boolean = false,
): Filterable, ListParameters<T>(value, withNull)

class UserTypeListParameters(
    value: List<UserType> = emptyList(),
    withNull: Boolean = false,
): Filterable, RawCodeListParameters<UserType>(value, withNull)
