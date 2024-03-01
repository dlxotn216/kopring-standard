package me.taesu.kopringstandard.user.domain

import me.taesu.kopringstandard.app.domain.CodeEnum
import me.taesu.kopringstandard.app.domain.RawCode

/**
 * Created by itaesu on 2022/05/24.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
enum class UserType(
    override val codeId: String = "USER_TYPE",
    override val description: String,
    override val code: String,
): CodeEnum, RawCode {
    BRONZE(description = "브론즈", code = "BRONZE"),
    SILVER(description = "실버", code = "SILVER"),
    GOLD(description = "골드", code = "GOLD"),
    PLATINUM(description = "플래티넘", code = "PLATINUM"),
    DIAMOND(description = "다이아몬드", code = "DIAMOND"),
    ;
}
