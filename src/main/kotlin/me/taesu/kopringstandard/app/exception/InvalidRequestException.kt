package me.taesu.kopringstandard.app.exception

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Created by itaesu on 2022/05/12.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
sealed class InvalidRequestException(
    val errorCode: ErrorCode
): RuntimeException() {
}

class UserEmailDuplicatedException(val duplicatedEmail: String): InvalidRequestException(
    ErrorCode.USER_ID_DUPLICATED
)

@OptIn(ExperimentalContracts::class)
fun validate(value: Boolean, lazyError: () -> InvalidRequestException) {
    contract {
        returns() implies value
    }
    if (!value) {
        throw lazyError()
    }
}