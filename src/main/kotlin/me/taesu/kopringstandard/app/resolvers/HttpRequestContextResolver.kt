package me.taesu.kopringstandard.app.resolvers

import me.taesu.kopringstandard.app.context.HttpRequestContext
import me.taesu.kopringstandard.app.context.HttpRequestContextHolder
import me.taesu.kopringstandard.app.exception.throwInvalidRequest
import me.taesu.kopringstandard.app.i18n.SupportLang
import org.springframework.context.annotation.Profile
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.LocaleResolver
import java.time.LocalDate
import java.util.*
import javax.servlet.http.HttpServletRequest

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
interface HttpRequestContextResolver: HandlerMethodArgumentResolver

@Profile("!test")
@Component
class RealHttpRequestContextResolver(
    private val localeResolver: LocaleResolver,
): HttpRequestContextResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return HttpRequestContext::class.java.isAssignableFrom(parameter.parameterType)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val locale = localeResolver.resolveLocale(webRequest.nativeRequest as HttpServletRequest)
        val supportLang = SupportLang.from(locale)
        HttpRequestContextHolder.set(supportLang)

        return when (parameter.parameterType) {
            HttpRequestContext::class.java -> HttpRequestContext(supportLang)

            else -> throwInvalidRequest("Unsupported parameter type: ${parameter.parameterType}")
        }
    }
}

@Profile("test")
@Component
class MockHttpRequestContextResolver: HttpRequestContextResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return HttpRequestContext::class.java.isAssignableFrom(parameter.parameterType)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val supportLang = webRequest.getHeader("Accept-Language")?.let { SupportLang.from(Locale.forLanguageTag(it)) }
            ?: SupportLang.DEFAULT_LANG
        HttpRequestContextHolder.set(supportLang)

        return when (parameter.parameterType) {
            HttpRequestContext::class.java -> HttpRequestContext(supportLang)

            else -> throwInvalidRequest(debugMessage = "Unsupported parameter type: ${parameter.parameterType}")
        }
    }
}
