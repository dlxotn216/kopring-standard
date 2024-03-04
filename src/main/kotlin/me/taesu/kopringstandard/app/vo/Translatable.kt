package me.taesu.kopringstandard.app.vo

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import me.taesu.kopringstandard.app.config.jackson.TranslatableSerializer
import me.taesu.kopringstandard.app.domain.I18nCode
import me.taesu.kopringstandard.app.domain.hash
import me.taesu.kopringstandard.app.exception.InvalidRequestException
import java.util.*

/**
 * Created by itaesu on 2024/03/02.
 *
 * 번역이 필요한 객체에 대한 인터페이스.
 * 객체 직렬화시에는 messageId를 기준으로 번역된 값을 반환한다.
 *
 * 원본 코드값이 같이 필요하다면 I18nCode를 사용한다.
 * @see me.taesu.kopringstandard.app.domain.I18nCode
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@JsonSerialize(
    using = TranslatableSerializer::class,
)
interface Translatable {
    val messageId: String?
    val messageArgs: Array<Any>
}

/**
 * messagePrefix.value 형태로 번역이 필요한 경우
 */
class SimpleI18n(
    private val messagePrefix: String,
    val value: String? = null,
    override val messageArgs: Array<Any> = emptyArray(),
): Translatable {
    constructor(i18nCode: I18nCode): this(i18nCode.messagePrefix, i18nCode.name)
    constructor(messagePrefix: String, value: Boolean): this(messagePrefix, value.toString().uppercase())
    constructor(messagePrefix: String, value: Enum<*>): this(messagePrefix, value.name)

    override val messageId
        get() = value?.let {
            "$messagePrefix.$it"
        }

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is SimpleI18n
                && other.messageId == this.messageId
                && other.messageArgs.contentEquals(this.messageArgs)
            )

    override fun hashCode() = hash(messageId, messageArgs.contentHashCode())
}

/**
 * messageId가 별도 가공 되지 않는 경우.
 */
class SimpleMessage(
    override val messageId: String,
    override val messageArgs: Array<Any> = emptyArray(),
): Translatable {
    constructor(e: InvalidRequestException): this(e.messageId, e.messageArgs)

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is SimpleMessage
                && other.messageId == this.messageId
                && other.messageArgs.contentEquals(this.messageArgs)
            )

    override fun hashCode() = hash(messageId, Arrays.hashCode(messageArgs))
}

/**
 * 번역이 필요없는 일반 텍스트. messageId가 serialize 된다.
 */
class SimpleText(
    override val messageId: String,
): Translatable {
    override val messageArgs: Array<Any> = emptyArray()

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is SimpleText
                && other.messageId == this.messageId
            )

    override fun hashCode() = hash(messageId)

    override fun toString() = "SimpleText($messageId)"
}
