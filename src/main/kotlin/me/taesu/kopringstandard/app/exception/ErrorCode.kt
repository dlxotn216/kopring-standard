package me.taesu.kopringstandard.app.exception

/**
 * Created by itaesu on 2022/05/12.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
enum class ErrorCode(
    val exceptionLevel: ExceptionLevel,
) {
    INVALID_REQUEST(ExceptionLevel.WARN),
    INVALID_PARAMETER(ExceptionLevel.WARN),
    REQUIRED_PARAMETER(ExceptionLevel.WARN),
    USER_ID_DUPLICATED(ExceptionLevel.NORMAL),
    UNEXPECTED_DATA_ALREADY_EXISTS(ExceptionLevel.FATAL),
    MAXIMUM_FILE_SIZE_EXCEEDED(ExceptionLevel.NORMAL),
    INVALID_USER_TYPE(ExceptionLevel.NORMAL),
    UNEXPECTED(ExceptionLevel.FATAL),
    ;

    val messageId: String = "EXCEPTION.${name}"

}

enum class ExceptionLevel {
    WARN,
    NORMAL,
    FATAL,
    ;
}
