package me.taesu.kopringstandard.app.config.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import me.taesu.kopringstandard.app.i18n.MessageService
import me.taesu.kopringstandard.app.i18n.SupportLang
import me.taesu.kopringstandard.app.vo.Translatable
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@Component
class TranslatableSerializer(private val messageService: MessageService): JsonSerializer<Translatable?>() {
    override fun serialize(i18n: Translatable?, generator: JsonGenerator, provider: SerializerProvider) {
        val label = i18n?.let {
            messageService.getMessage(i18n, SupportLang.from(LocaleContextHolder.getLocale()))
        }
        generator.writeString(label)
    }
}

