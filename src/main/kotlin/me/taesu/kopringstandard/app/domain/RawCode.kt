package me.taesu.kopringstandard.app.domain

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import me.taesu.kopringstandard.app.config.jackson.RawCodeSerializer

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */
@JsonSerialize(using = RawCodeSerializer::class)
interface RawCode {
    val code: String
}
