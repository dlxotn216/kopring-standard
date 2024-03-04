package me.taesu.kopringstandard.app.domain

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import me.taesu.kopringstandard.app.config.jackson.I18nCodeSerializer

/**
 * Created by itaesu on 2022/05/24.
 *
 * { value: 'code', label: 'i18n label' }의 형태로 번역이 필요한 객체에 대한 인터페이스.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@JsonSerialize(
    using = I18nCodeSerializer::class,
    nullsUsing = I18nCodeSerializer::class
)
interface I18nCode {
    val codeId: String
    val name: String
    val messagePrefix: String get() = "CODE.$codeId"
    val messageId: String get() = "CODE.$codeId.$name"
    val description: String
}
