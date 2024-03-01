package me.taesu.kopringstandard.app.config.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.type.ClassKey
import me.taesu.kopringstandard.app.converters.RawCodeConverter.Companion.fromRawCode
import me.taesu.kopringstandard.app.domain.RawCode
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@Component
class RawCodeSerializer: JsonSerializer<RawCode?>() {
    override fun serialize(value: RawCode?, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value?.code)
    }
}

class RawCodeDeserializerModule: Module() {
    override fun getModuleName(): String {
        return "RawCode"
    }

    override fun version(): Version {
        return Version.unknownVersion()
    }

    override fun setupModule(context: SetupContext) {
        context.addDeserializers(object: Deserializers.Base() {
            val cache: MutableMap<ClassKey, JsonDeserializer<*>> =
                ConcurrentHashMap<ClassKey, JsonDeserializer<*>>()

            override fun findEnumDeserializer(
                type: Class<*>,
                config: DeserializationConfig,
                beanDesc: BeanDescription,
            ): JsonDeserializer<*>? {
                if (RawCode::class.java.isAssignableFrom(type)) {
                    val enumDeserializer: JsonDeserializer<*> = RawCodeDeserializer(type)
                    addDeserializer(type, enumDeserializer)
                    return enumDeserializer
                }
                return null
            }

            override fun hasDeserializerFor(config: DeserializationConfig, valueType: Class<*>?): Boolean {
                return cache.containsKey(ClassKey(valueType))
            }

            fun addDeserializer(forClass: Class<*>?, deserializer: JsonDeserializer<*>) {
                val key = ClassKey(forClass)
                cache[key] = deserializer
            }
        })
    }
}

class RawCodeDeserializer(valueClass: Class<*>? = null): StdDeserializer<Enum<*>>(valueClass), ContextualDeserializer {
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): Enum<*> {
        val enumType = _valueClass as Class<out Enum<*>>
        val jsonNode = jp.codec.readTree<JsonNode>(jp)
        val text = jsonNode.asText()
        return fromRawCode(enumType, text)
    }

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty): JsonDeserializer<*> {
        return RawCodeDeserializer(property.type.rawClass)
    }
}
