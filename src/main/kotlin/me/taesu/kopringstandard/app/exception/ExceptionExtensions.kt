package me.taesu.kopringstandard.app.exception

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
fun Throwable.resolveMessage(
    fallbackMessage: String = "Unknown error messages.",
) = (message ?: localizedMessage) ?: fallbackMessage
