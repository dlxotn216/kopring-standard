package me.taesu.kopringstandard.app.exception

import org.springframework.http.HttpStatus

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
fun throwInvalidRequest(
    debugMessage: String,
    errorCode: ErrorCode = ErrorCode.INVALID_REQUEST,
    messageId: String = errorCode.messageId,
    messageArgs: Array<Any> = emptyArray(),
    statusCode: HttpStatus = HttpStatus.BAD_REQUEST,
    fieldName: String? = null,
): Nothing = throw InvalidRequestException(
    errorCode = errorCode,
    messageId = messageId,
    messageArgs = messageArgs,
    debugMessage = debugMessage,
    statusCode = statusCode,
    fieldName = fieldName,
)

fun throwInvalidUserType(
    debugMessage: String,
    messageId: String,
    messageArgs: Array<Any> = emptyArray(),
): Nothing = throwInvalidRequest(
    errorCode = ErrorCode.INVALID_USER_TYPE,
    debugMessage = debugMessage,
    messageId = messageId,
    messageArgs= messageArgs,
)
