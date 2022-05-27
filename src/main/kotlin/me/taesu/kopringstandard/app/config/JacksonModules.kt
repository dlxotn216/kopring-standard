package me.taesu.kopringstandard.app.config

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import me.taesu.kopringstandard.app.domain.CodeEnum
import me.taesu.kopringstandard.app.interfaces.CodeEnumNullSerializer

/**
 * Created by itaesu on 2022/05/25.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
class CodeEnumModule: Module() {
    override fun version(): Version = Version.unknownVersion()
    override fun getModuleName() = "CodeEnum"
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
        beanDesc: BeanDescription, beanProperties: List<BeanPropertyWriter>
    ): List<BeanPropertyWriter> {
        val codeEnumNullSerializer = CodeEnumNullSerializer()
        return beanProperties.apply {
            filter { CodeEnum::class.java.isAssignableFrom(it.type.rawClass) }
                .forEach { it.assignNullSerializer(codeEnumNullSerializer) }
        }
    }
}
