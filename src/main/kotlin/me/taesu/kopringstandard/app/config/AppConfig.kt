package me.taesu.kopringstandard.app.config

import me.taesu.kopringstandard.app.config.jackson.CodeEnumModule
import me.taesu.kopringstandard.app.config.jackson.RawCodeDeserializerModule
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
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

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/resources/**")
            .addResourceLocations("/resources/")
    }

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

    @Bean
    fun rawCodeDeserializerModule() =  RawCodeDeserializerModule()
}


