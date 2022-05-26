package me.taesu.kopringstandard.app.config

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import me.taesu.kopringstandard.app.domain.CodeEnum
import me.taesu.kopringstandard.app.interfaces.CodeEnumNullSerializer
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver


/**
 * Created by itaesu on 2022/05/24.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@Configuration
class AppConfig: WebMvcConfigurer {
    @Bean
    fun localResolver(): LocaleResolver = AcceptHeaderLocaleResolver()

    @Bean
    fun messageSource(): MessageSource {
        return ReloadableResourceBundleMessageSource().apply {
            this.setBasename("classpath:messages/message")
            this.setDefaultEncoding("UTF-8")
        }
    }

    @Bean
    fun codeEnumModule() = CodeEnumModule()
}

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
        for (beanProperty in beanProperties) {
            if (CodeEnum::class.java.isAssignableFrom(beanProperty.type.rawClass)) {
                beanProperty.assignNullSerializer(CodeEnumNullSerializer())
            }
        }
        return beanProperties
    }
}