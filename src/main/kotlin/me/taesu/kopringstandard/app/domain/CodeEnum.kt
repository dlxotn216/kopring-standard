package me.taesu.kopringstandard.app.domain

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import me.taesu.kopringstandard.app.interfaces.CodeEnumSerializer

/**
 * Created by itaesu on 2022/05/24.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
@JsonSerialize(
    using = CodeEnumSerializer::class,
    nullsUsing = CodeEnumSerializer::class
)
interface CodeEnum {
    val codeId: String
    val name: String
    val messageId: String get() = "CODE.$codeId.$name"
}