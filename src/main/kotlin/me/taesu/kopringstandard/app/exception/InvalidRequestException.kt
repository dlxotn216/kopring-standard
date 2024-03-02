package me.taesu.kopringstandard.app.exception

import org.springframework.http.HttpStatus
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Created by itaesu on 2022/05/12.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
open class InvalidRequestException(
    val errorCode: ErrorCode,
    val debugMessage: String,
    val messageId: String = errorCode.messageId,
    val messageArgs: Array<Any> = emptyArray(),
    val statusCode: HttpStatus = HttpStatus.BAD_REQUEST,
    val fieldName: String? = null,
): RuntimeException(debugMessage)

class UserEmailDuplicatedException(val duplicatedEmail: String): InvalidRequestException(
    ErrorCode.USER_ID_DUPLICATED, "user email[$duplicatedEmail] is duplicated."
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
