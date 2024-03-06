package me.taesu.kopringstandard.app.context

import me.taesu.kopringstandard.app.domain.hash
import me.taesu.kopringstandard.app.i18n.SupportLang
import org.springframework.stereotype.Component

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@Component
class HttpRequestContextHolder {
    fun getSupportLang(): SupportLang = LOCAL.get()?.supportLang ?: SupportLang.EN

    companion object {
        private val LOCAL = ThreadLocal<HttpRequestContext>()

        fun remove() {
            LOCAL.remove()
        }

        fun set(supportLang: SupportLang) {
            LOCAL.set(HttpRequestContext(supportLang))
        }
    }
}

open class HttpRequestContext(
    open val supportLang: SupportLang,
) {
    override fun equals(other: Any?): Boolean =
        this === other || (
            other is HttpRequestContext
                && other.supportLang == this.supportLang
            )

    override fun hashCode() = hash(supportLang)
}
