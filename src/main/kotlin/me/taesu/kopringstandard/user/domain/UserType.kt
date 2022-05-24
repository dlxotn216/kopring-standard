package me.taesu.kopringstandard.user.domain

import me.taesu.kopringstandard.app.exception.domain.CodeEnum

/**
 * Created by itaesu on 2022/05/24.
 *
 * @author Lee Tae Su
 * @version ConsentV3 v1.0 wB202203
 * @since ConsentV3 v1.0 wB202203
 */
enum class UserType: CodeEnum {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM,
    DIAMOND,
    ;
}