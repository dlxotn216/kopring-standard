package me.taesu.kopringstandard.app.i18n

import me.taesu.kopringstandard.app.domain.I18nCode
import me.taesu.kopringstandard.app.exception.ErrorCode
import me.taesu.kopringstandard.app.exception.InvalidRequestException
import me.taesu.kopringstandard.app.vo.SimpleText
import me.taesu.kopringstandard.app.vo.Translatable
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@Component
class MessageService(private val messageSource: MessageSource) {
    fun getMessage(
        e: InvalidRequestException,
        supportLang: SupportLang,
    ) = getMessage(e.messageId, supportLang, e.messageArgs)

    fun getMessage(
        errorCode: ErrorCode,
        supportLang: SupportLang,
        args: Array<Any>? = null,
    ) = getMessage(errorCode.messageId, supportLang, args)

    fun getMessage(
        messageId: String,
        supportLang: SupportLang,
        args: Array<Any>? = null,
    ): String {
        val newArgs = args?.map { arg ->
            when (arg) {
                is Translatable -> arg.messageId?.let { getMessage(it, supportLang, arg.messageArgs) }
                is LocalDate -> arg.format(DateTimeFormatter.ISO_DATE)
                is Number -> arg.toString()
                else -> arg
            }
        }?.toTypedArray() ?: args
        return messageSource.getMessage(messageId, newArgs, supportLang.locale)
    }

    fun getMessage(
        transformable: Translatable,
        supportLang: SupportLang,
    ): String? {
        if (transformable is SimpleText) {
            return transformable.messageId
        }
        val messageId = transformable.messageId ?: return null
        return getMessage(messageId, supportLang, transformable.messageArgs)
    }

    fun getMessage(
        i18nCode: I18nCode,
        supportLang: SupportLang,
    ): String? {
        val messageId = i18nCode.messageId
        return getMessage(messageId, supportLang)
    }
}

