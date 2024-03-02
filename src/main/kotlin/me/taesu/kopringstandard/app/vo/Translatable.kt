package me.taesu.kopringstandard.app.vo

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import me.taesu.kopringstandard.app.config.jackson.TranslatableSerializer
import me.taesu.kopringstandard.app.domain.hash
import java.util.*

/**
 * Created by itaesu on 2024/03/02.
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

class SimpleI18n(
    private val messagePrefix: String,
    val value: String? = null,
    override val messageArgs: Array<Any> = emptyArray(),
): Translatable {
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

class SimpleMessage(
    override val messageId: String,
    override val messageArgs: Array<Any> = emptyArray(),
): Translatable {
    override fun equals(other: Any?): Boolean =
        this === other || (
            other is SimpleMessage
                && other.messageId == this.messageId
                && other.messageArgs.contentEquals(this.messageArgs)
            )

    override fun hashCode() = hash(messageId, Arrays.hashCode(messageArgs))
}

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
