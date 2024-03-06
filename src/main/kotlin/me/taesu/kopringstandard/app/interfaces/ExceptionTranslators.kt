package me.taesu.kopringstandard.app.interfaces

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import me.taesu.kopringstandard.app.exception.ErrorCode
import me.taesu.kopringstandard.app.exception.InvalidRequestException
import me.taesu.kopringstandard.app.exception.resolveMessage
import me.taesu.kopringstandard.app.vo.SimpleMessage
import me.taesu.kopringstandard.app.vo.Translatable

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
fun toFailResponse(
    e: InvalidRequestException,
): FailResponse {
    return FailResponse(
        errorCode = e.errorCode,
        errorMessage = SimpleMessage(e),
        debugMessage = e.resolveMessage(),
        additional = mutableMapOf<String, Any>().apply {
            e.fieldName?.let {
                put("fieldName", it)
            }
        }
    )
}

fun toFailResponse(
    e: MissingKotlinParameterException,
): FailResponse {
    val fieldName = e.parameter.name ?: ""
    val errorCode = ErrorCode.REQUIRED_PARAMETER
    return toFieldError(
        errorCode = errorCode,
        fieldName = fieldName,
        debugMessage = e.resolveMessage(),
        message = SimpleMessage(errorCode.messageId),
    )
}

fun toFailResponse(
    e: JsonMappingException,
): FailResponse {
    return when (val cause = e.cause) {
        // init 블럭에서 예외가 던져진 경우
        is InvalidRequestException -> toFailResponse(cause)

        else -> {
            val errorCode = ErrorCode.REQUIRED_PARAMETER
            toFieldError(
                errorCode,
                e.path.firstOrNull()?.fieldName,
                e.resolveMessage(),
                SimpleMessage(errorCode.messageId)
            )
        }
    }
}

fun toFieldError(
    errorCode: ErrorCode,
    fieldName: String? = null,
    debugMessage: String,
    message: Translatable,
): FailResponse {
    return FailResponse(
        errorCode = errorCode,
        errorMessage = message,
        debugMessage = debugMessage,
        additional = mapOf("field" to (fieldName ?: "unknown")),
    )
}
