package me.taesu.kopringstandard.user.domain

import me.taesu.kopringstandard.app.domain.CodeEnum

/**
 * Created by itaesu on 2022/05/24.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
enum class UserType(
    override val codeId: String = "USER_TYPE",
    override val description: String
): CodeEnum {
    BRONZE(description = "브론즈"),
    SILVER(description = "실버"),
    GOLD(description = "골드"),
    PLATINUM(description = "플래티넘"),
    DIAMOND(description = "다이아몬드"),
    ;
}