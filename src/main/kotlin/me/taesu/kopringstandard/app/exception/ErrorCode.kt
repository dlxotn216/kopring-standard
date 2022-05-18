package me.taesu.kopringstandard.app.exception

/**
 * Created by itaesu on 2022/05/12.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
enum class ErrorCode(val clazz: Class<out InvalidRequestException>) {
    USER_ID_DUPLICATED(UserEmailDuplicatedException::class.java)
}