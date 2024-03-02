package me.taesu.kopringstandard.app.config

import me.taesu.kopringstandard.app.config.jackson.I18nCodeModule
import me.taesu.kopringstandard.app.config.jackson.RawCodeDeserializerModule
import me.taesu.kopringstandard.app.converters.RawCodeConverter
import me.taesu.kopringstandard.app.i18n.SupportLang
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.convert.converter.Converter
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
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
    @Autowired
    private lateinit var rawConverters: List<RawCodeConverter<*>>

    override fun addFormatters(registry: FormatterRegistry) {
        super.addFormatters(registry)
        rawConverters.filterIsInstance<Converter<*, *>>().forEach {
            registry.addConverter(it)
        }
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/resources/**")
            .addResourceLocations("/resources/")
    }

    @Bean
    fun localeResolver(): LocaleResolver = AcceptHeaderLocaleResolver().apply {
        supportedLocales = SupportLang.values().map { it.locale }
        defaultLocale = SupportLang.DEFAULT_LOCALE
    }

    @Bean
    fun messageSource(): MessageSource {
        return ReloadableResourceBundleMessageSource().apply {
            this.setUseCodeAsDefaultMessage(true)
            this.setAlwaysUseMessageFormat(true)
            this.setBasename("classpath:messages/message")
            this.setDefaultEncoding("UTF-8")
        }
    }

    @Bean
    fun codeEnumModule() = I18nCodeModule()

    @Bean
    fun rawCodeDeserializerModule() = RawCodeDeserializerModule()
}


