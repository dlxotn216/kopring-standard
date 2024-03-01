package me.taesu.kopringstandard.app.converters

import me.taesu.kopringstandard.app.domain.RawCode
import me.taesu.kopringstandard.user.domain.UserStatus
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import javax.persistence.AttributeConverter

/**
 * Created by itaesu on 2024/03/01.
 *
 * @author Lee Tae Su
 * @version kopring-standard
 * @since kopring-standard
 */

open class RawCodeConverter<E>(val valueClass: Class<*>):
    AttributeConverter<E?, String> where E: Enum<E>, E: RawCode? {
    fun convertRaw(rawCode: String): Enum<*> {
        return fromRawCode(valueClass as Class<out Enum<*>>, rawCode)
    }

    override fun convertToEntityAttribute(dbData: String?): E? {
        dbData?: return null
        return convertRaw(dbData) as E
    }

    override fun convertToDatabaseColumn(attribute: E?): String? {
        return attribute?.code
    }

    companion object {
        fun fromRawCode(enumType: Class<out Enum<*>>, rawCode: String): Enum<*> {
            val enum = enumType.enumConstants
                .filterIsInstance(RawCode::class.java)
                .firstOrNull { it.code == rawCode }
                as? Enum<*>
                ?: throw IllegalArgumentException(
                    String.format(
                        "[%s][%s]값에 해당하는 Enum 찾지 못했습니다.",
                        enumType.name,
                        rawCode
                    )
                )
            return java.lang.Enum.valueOf(enumType, enum.name)
        }
    }
}

@Component
class UserStatusConverter: Converter<String, UserStatus>, RawCodeConverter<UserStatus>(UserStatus::class.java) {
    override fun convert(source: String): UserStatus? {
        return super.convertToEntityAttribute(source)
    }
}
