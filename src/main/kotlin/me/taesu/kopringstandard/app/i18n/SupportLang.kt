package me.taesu.kopringstandard.app.i18n

import java.util.*

/**
 * Created by itaesu on 2024/03/02.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
enum class SupportLang(val locale: Locale, val code: String) {
    EN(Locale.forLanguageTag("en"), "en"),
    JA(Locale.forLanguageTag("ja"), "ja"),
    ZH(Locale.forLanguageTag("zh"), "zh"),
    KO(Locale.forLanguageTag("ko"), "ko"),
    ;

    companion object {
        val DEFAULT_LANG = EN
        val DEFAULT_LOCALE = DEFAULT_LANG.locale

        fun from(locale: Locale?): SupportLang {
            return values().find { it.locale == locale } ?: DEFAULT_LANG
        }
    }
}
