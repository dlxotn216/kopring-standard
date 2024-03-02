package me.taesu.kopringstandard.app.config.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import me.taesu.kopringstandard.app.domain.I18nCode
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

/**
 * Created by itaesu on 2022/05/25.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
class I18nCodeModule: Module() {
    override fun version(): Version = Version.unknownVersion()
    override fun getModuleName() = "I18nCode"
    override fun setupModule(context: SetupContext) {
        context.addBeanSerializerModifier(CustomBeanSerializerModifier())
    }
}

/**
 * Created by itaesu on 2022/05/25.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
class CustomBeanSerializerModifier: BeanSerializerModifier() {
    override fun changeProperties(
        config: SerializationConfig,
        beanDesc: BeanDescription, beanProperties: List<BeanPropertyWriter>,
    ): List<BeanPropertyWriter> {
        val i18nCodeNullSerializer = I18nCodeNullSerializer()
        return beanProperties.apply {
            filter { I18nCode::class.java.isAssignableFrom(it.type.rawClass) }
                .forEach { it.assignNullSerializer(i18nCodeNullSerializer) }
        }
    }
}

/**
 * Created by itaesu on 2022/05/25.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
class I18nCodeNullSerializer: JsonSerializer<Any?>() {
    override fun serialize(
        value: Any?, generator: JsonGenerator,
        provider: SerializerProvider,
    ) {
        generator.writeStartObject()
        generator.writeStringField("value", "")
        generator.writeStringField("label", "")
        generator.writeEndObject()
    }
}

/**
 * Created by itaesu on 2022/05/24.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@Component
class I18nCodeSerializer(var messageSource: MessageSource): JsonSerializer<I18nCode?>() {
    override fun serialize(code: I18nCode?, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeStartObject()
        generator.writeStringField("value", code?.name ?: "")
        val label = code?.let {
            messageSource.getMessage(it.messageId, emptyArray<Any>(), LocaleContextHolder.getLocale())
        } ?: ""
        generator.writeStringField("label", label)
        generator.writeEndObject()
    }
}
