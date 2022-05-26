package me.taesu.kopringstandard.app.interfaces

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import me.taesu.kopringstandard.app.domain.CodeEnum
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

/**
 * Created by itaesu on 2022/05/24.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@Component
class CodeEnumSerializer(var messageSource: MessageSource): JsonSerializer<CodeEnum?>() {

    override fun serialize(code: CodeEnum?, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeStartObject()
        generator.writeStringField("value", code?.name ?: "")
        val label = code?.let {
            messageSource.getMessage(it.messageId, emptyArray<Any>(), LocaleContextHolder.getLocale())
        } ?: ""
        generator.writeStringField("label", label)
        generator.writeEndObject()
    }
}

/**
 * Created by itaesu on 2022/05/25.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
class CodeEnumNullSerializer: JsonSerializer<Any?>() {
    override fun serialize(
        value: Any?, generator: JsonGenerator,
        provider: SerializerProvider
    ) {
        generator.writeStartObject()
        generator.writeStringField("value", "")
        generator.writeStringField("label", "")
        generator.writeEndObject()
    }
}