package me.taesu.kopringstandard.app.interfaces

import me.taesu.kopringstandard.app.exception.ErrorCode
import me.taesu.kopringstandard.app.query.PageableCriteria
import me.taesu.kopringstandard.app.query.PaginatedRow
import me.taesu.kopringstandard.app.vo.SimpleText
import me.taesu.kopringstandard.app.vo.Translatable

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
sealed class Response(
    status: ResponseStatus,
    val message: Translatable,
) {
    val status: String = status.code
}

enum class ResponseStatus(val code: String) {
    SUCCESS("success"),
    FAIL("fail")
    ;
}

class SuccessResponse<T: Any?>(
    val result: T,
    message: Translatable,
): Response(
    status = ResponseStatus.SUCCESS,
    message = message,
)

class PaginatedResult(
    val contents: List<PaginatedRow>,
    val totalPages: Int,
    val totalCount: Long,
    val isFirst: Boolean,
    val isLast: Boolean,
    val page: Int,
    val size: Int,
    val isEmpty: Boolean,
    val isFiltered: Boolean,
    val filteredElements: PageableCriteria,
    val additional: Map<String, Any>,
)

class FailResponse(
    errorCode: ErrorCode,
    debugMessage: String,
    errorMessage: Translatable = SimpleText("Request was failed."),
    additional: Map<String, Any> = emptyMap(),
): Response(
    status = ResponseStatus.FAIL,
    message = errorMessage,
) {

    val error: Map<String, Any> = mutableMapOf<String, Any>(
        "errorCode" to errorCode.name,
        "debugMessage" to debugMessage
    ).apply {
        putAll(additional)
    }
}
